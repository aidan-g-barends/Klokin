package com.klokin.backend.config;

import com.klokin.backend.domain.Client;
import com.klokin.backend.domain.Company;
import com.klokin.backend.domain.Employee;
import com.klokin.backend.domain.EventSource;
import com.klokin.backend.domain.EventType;
import com.klokin.backend.domain.Role;
import com.klokin.backend.domain.TimeEvent;
import com.klokin.backend.domain.WorkLocation;
import com.klokin.backend.factory.ClientFactory;
import com.klokin.backend.factory.CompanyFactory;
import com.klokin.backend.factory.EmployeeFactory;
import com.klokin.backend.factory.TimeEventFactory;
import com.klokin.backend.factory.WorkLocationFactory;
import com.klokin.backend.repository.ClientRepository;
import com.klokin.backend.repository.CompanyRepository;
import com.klokin.backend.repository.EmployeeRepository;
import com.klokin.backend.repository.TimeEventRepository;
import com.klokin.backend.repository.WorkLocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

// Fills the empty dev database with a demo company and about six weeks of realistic clock-ins,
// so every endpoint has something to show the moment the app starts.
// @Profile("dev"): this class doesn't exist in production.
@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkLocationRepository workLocationRepository;
    private final ClientRepository clientRepository;
    private final TimeEventRepository timeEventRepository;
    private final Clock clock;

    public DevDataSeeder(CompanyRepository companyRepository, EmployeeRepository employeeRepository,
                         WorkLocationRepository workLocationRepository, ClientRepository clientRepository,
                         TimeEventRepository timeEventRepository, Clock clock) {
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
        this.workLocationRepository = workLocationRepository;
        this.clientRepository = clientRepository;
        this.timeEventRepository = timeEventRepository;
        this.clock = clock;
    }

    // CommandLineRunner: Spring calls run() once, right after the app has started
    @Override
    public void run(String... args) {
        if (companyRepository.count() > 0) {
            return; // only ever seed an empty database
        }

        Company company = companyRepository.save(CompanyFactory.create("Demo Signs & Print",
                "Africa/Johannesburg", LocalTime.of(8, 0), LocalTime.of(17, 0),
                LocalTime.of(9, 0), LocalTime.of(13, 0), 60));
        WorkLocation workshop = workLocationRepository.save(
                WorkLocationFactory.create(company, "Workshop", -33.0930, 18.0340, 150, "DemoSigns-WiFi"));
        List<Client> clients = clientRepository.saveAll(List.of(
                ClientFactory.create(company, "Harbour Cafe", "Bree Street"),
                ClientFactory.create(company, "Lagoon Pharmacy", "Main Road"),
                ClientFactory.create(company, "West Coast Motors", "Industrial Area")));
        List<Employee> staff = employeeRepository.saveAll(List.of(
                EmployeeFactory.create(company, "Lee", "Jacobs", "lee@demosigns.co.za", "0821234567", Role.EMPLOYEE),
                EmployeeFactory.create(company, "Ruan", "Pretorius", null, "073 123 4567", Role.EMPLOYEE),
                EmployeeFactory.create(company, "Thandi", "Mokoena", "thandi@demosigns.co.za", null, Role.MANAGER)));

        // Fixed seed: the "random" demo data comes out the same on every run
        Seed seed = new Seed(company, workshop, clients, new Random(42), new ArrayList<>());
        ZoneId zone = company.zoneId();
        LocalDate today = LocalDate.now(clock.withZone(zone));
        LocalDate firstDay = today.minusMonths(1).withDayOfMonth(1);

        for (Employee employee : staff) {
            for (LocalDate date = firstDay; date.isBefore(today); date = date.plusDays(1)) {
                seedPastDay(seed, employee, date);
            }
            // Today: a full day if it's already evening, otherwise clocked in this morning
            // (so the live feed and the IN_PROGRESS status have something to show)
            LocalTime timeNow = LocalTime.now(clock.withZone(zone));
            boolean workingDay = company.scheduledMinutesOn(today.getDayOfWeek()) > 0;
            if (timeNow.isAfter(LocalTime.of(19, 0))) {
                seedPastDay(seed, employee, today);
            } else if (workingDay && timeNow.isAfter(LocalTime.of(8, 0))) {
                seed.add(employee, today, LocalTime.of(7, 55), EventType.CLOCK_IN, EventSource.AUTO_WIFI,
                        workshop.getLatitude(), workshop.getLongitude(), null);
            }
        }

        timeEventRepository.saveAll(seed.events());
        log.info("Seeded demo data: {} staff, {} events. Try GET /api/employees/{}/attendance",
                staff.size(), seed.events().size(), staff.get(0).getId());
    }

    private void seedPastDay(Seed seed, Employee employee, LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        Random random = seed.random();
        if (seed.company().scheduledMinutesOn(day) == 0) {
            return; // Sunday
        }
        boolean saturday = day == DayOfWeek.SATURDAY;
        if (saturday && random.nextBoolean()) {
            return; // Saturday rota: roughly half the staff work each Saturday
        }
        if (random.nextInt(25) == 0) {
            return; // the odd sick day, shows as ABSENT
        }

        double shopLat = seed.workshop().getLatitude();
        double shopLng = seed.workshop().getLongitude();

        LocalTime in = saturday ? LocalTime.of(8, 50 + random.nextInt(10)) : LocalTime.of(7, 45 + random.nextInt(15));
        if (employee.getFirstName().equals("Ruan") && date.getDayOfMonth() == 5) {
            // Location switched off once: shows up flagged NO_LOCATION in the feed
            seed.add(employee, date, in, EventType.CLOCK_IN, EventSource.MANUAL, null, null, null);
        } else {
            seed.add(employee, date, in, EventType.CLOCK_IN, EventSource.AUTO_WIFI, shopLat, shopLng, null);
        }

        // About a third of weekdays include an install at a client's site
        if (!saturday && random.nextInt(3) == 0) {
            Client client = seed.clients().get(random.nextInt(seed.clients().size()));
            LocalTime arrive = LocalTime.of(10, random.nextInt(30));
            seed.add(employee, date, arrive, EventType.SITE_ARRIVE, EventSource.MANUAL,
                    shopLat + 0.012, shopLng - 0.008, client);
            seed.add(employee, date, arrive.plusMinutes(90 + random.nextInt(60)), EventType.SITE_LEAVE,
                    EventSource.MANUAL, shopLat + 0.012, shopLng - 0.008, client);
        }

        if (!saturday) {
            LocalTime lunch = LocalTime.of(13, random.nextInt(10));
            seed.add(employee, date, lunch, EventType.LUNCH_START, EventSource.MANUAL, null, null, null);
            seed.add(employee, date, lunch.plusMinutes(40 + random.nextInt(20)), EventType.LUNCH_END,
                    EventSource.MANUAL, null, null, null);
        }

        if (employee.getFirstName().equals("Lee") && date.getDayOfMonth() == 12) {
            return; // forgot to clock out: shows as INCOMPLETE on the heatmap
        }

        // Most days finish around 17:00; about one in four runs an hour or two over
        LocalTime out = saturday
                ? LocalTime.of(13, random.nextInt(15))
                : LocalTime.of(17, 0).plusMinutes(random.nextInt(4) == 0 ? 60 + random.nextInt(60) : random.nextInt(15));
        seed.add(employee, date, out, EventType.CLOCK_OUT, EventSource.AUTO_GPS, shopLat, shopLng, null);
    }

    // Holds what every seeded event needs, so the helper methods don't need ten parameters each
    private record Seed(Company company, WorkLocation workshop, List<Client> clients,
                        Random random, List<TimeEvent> events) {

        void add(Employee employee, LocalDate date, LocalTime time, EventType type, EventSource source,
                 Double latitude, Double longitude, Client client) {
            events.add(TimeEventFactory.create(UUID.randomUUID().toString(), employee, type, source,
                    date.atTime(time).atZone(company.zoneId()).toInstant(),
                    latitude, longitude, client, null, List.of(workshop)));
        }
    }
}
