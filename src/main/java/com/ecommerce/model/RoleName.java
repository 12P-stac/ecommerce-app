package com.ecommerce.model;

public enum RoleName {
    USER,
    ADMIN,
    SELLER;

    public boolean startsWith(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startsWith'");
    }
   public class RoleNameExample {
       public static void main(String[] args) {
           RoleName role = RoleName.ADMIN;
           System.out.println("Role: " + role);
       }
   }  

}
