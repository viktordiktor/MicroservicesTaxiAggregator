Feature: Driver Service

  Scenario: Get Driver by existing ID
    Given Driver with ID 1 exists
    When getDriverById method is called with ID 1
    Then DriverResponse should contains driver with ID 1
  Scenario: Get Driver by not existing ID
    Given Driver with ID 999 not exists
    When getDriverById method is called with ID 999
    Then DriverNotFoundException should thrown for ID 999

  Scenario: Create Driver by not existing username and not existing phone
    Given Driver with username "viktordiktor" and phone "+375111111111" not exists
    When createDriver method is called with DriverRequest of username "viktordiktor" and phone "+375111111111"
    Then DriverResponse should contains driver with username "viktordiktor" and phone "+375111111111"
  Scenario: Create Driver by existing username and not existing phone
    Given Driver with existing username "JonhDoe" and not existing phone "+375484848484"
    When createDriver method is called with DriverRequest of username "JonhDoe" and phone "+375484848484"
    Then UsernameAlreadyExistsException should thrown for username "JonhDoe" and phone "+375484848484"
  Scenario: Create Driver by not existing username and existing phone
    Given Driver with not existing username "viktordiktor" and existing phone "+375111111111"
    When createDriver method is called with DriverRequest of username "375111111111" and phone "+375111111111"
    Then PhoneAlreadyExistsException should thrown for username "375111111111" and phone "+375111111111"

  Scenario: Edit Driver by existing ID and not existing username and not existing phone
    Given Driver with ID 3 exists and Driver with username "USERNAME2" and phone "+375222222222" not exists
    When editDriver method is called with ID 3 and DriverRequest of username "USERNAME2" and phone "+375222222222"
    Then DriverResponse should contains driver with ID 3 and username "USERNAME2" and phone "+375222222222"
  Scenario: Edit Driver by existing ID and existing username and not existing phone
    Given Driver with ID 3 exists and Driver with existing username "JonhDoe" and not existing phone "+375222222222"
    When editDriver method is called with ID 3 and DriverRequest of username "JonhDoe" and phone "+375222222222"
    Then UsernameAlreadyExistsException should thrown for username "JonhDoe" and phone "+375484848484"
  Scenario: Edit Driver by existing ID and not existing username and existing phone
    Given Driver with ID 3 exists and Driver with not existing username "USERNAME2" and existing phone "+375111111111"
    When editDriver method is called with ID 3 and DriverRequest of username "USERNAME2" and phone "+375111111111"
    Then PhoneAlreadyExistsException should thrown for username "USERNAME2" and phone "+375111111111"
  Scenario: Edit Driver by not existing ID and not existing username and not existing phone
    Given Driver with ID 999 not exists and Driver with username "USERNAME2" and phone "+375222222222" not exists
    When editDriver method is called with ID 999 and DriverRequest of username "USERNAME2" and phone "+375222222222"
    Then DriverNotFoundException should thrown for ID 999

  Scenario: Delete Driver by existing ID
    Given Driver with ID 1 exists
    When deleteDriver method is called with ID 1
    Then Should return No Content for ID 1 and delete Driver
  Scenario: Delete Driver by not existing ID
    Given Driver with ID 999 not exists
    When deleteDriver method is called with ID 999
    Then DriverNotFoundException should thrown for ID 999  