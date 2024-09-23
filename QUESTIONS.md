## Questions Regarding the Congestion Tax Assignment

1. Clarification on City-Specific Tax Rules
    - In the bonus scenario, it's mentioned that the application should work for different cities with varying tax rules. Are there specific requirements or variations for other cities that need to be handled beyond the configuration for Gothenburg? For example:
      - Do different cities have different maximum daily tax limits?
      - Do the exempt vehicle rules vary by city?
      - Should we support multiple currencies (if the application is intended for cities outside Sweden)?
     
2. Public Holidays and Custom Exceptions
   - Is there a standardized method for determining public holidays, or will these be part of the external configuration (e.g., in the JSON file)?
   - How should we handle "days before public holidays" dynamically, given that holidays may vary from year to year?

## Assumptions Made:

 - The tax rules and exemptions provided are specific to Gothenburg, and future cities can have different configurations that will be externalized.
 - Public holidays and special days (e.g., days before public holidays) for 2013 were assumed to be static for the purpose of the assignment, but would need dynamic handling for real-world usage.
 - Exempt vehicle types are assumed to be the same across all cities unless otherwise specified.
