package com.klokin.backend.domain;

// What someone may do in Klokin. EMPLOYEE sees only their own data, MANAGER approves for the team,
// OWNER sees everything. (Enforced once we add security.)
public enum Role {
    EMPLOYEE,
    MANAGER,
    OWNER
}
