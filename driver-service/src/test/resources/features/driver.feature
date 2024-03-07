Feature: Driver Service

  Scenario: Get Driver by existing ID
    Given Driver with ID 1 exists
    When getDriverById method is called with ID 1
    Then DriverResponse should contains driver with ID 1
  Scenario: Get Driver by not existing ID
    Given Driver with ID 999 not exists
    When getDriverById method is called with ID 999
    Then DriverNotFoundException should be thrown for ID 999

  Scenario: Create Driver by not existing username and not existing phone
    Given Driver with username "viktordiktor" and phone "+375111111111" not exists
    When createDriver method is called with DriverRequest of username "viktordiktor" and phone "+375111111111"
    Then DriverResponse should contains driver with username "viktordiktor" and phone "+375111111111"
  Scenario: Create Driver by existing username and not existing phone
    Given Driver with existing username "JonhDoe" and not existing phone "+375484848484"
    When createDriver method is called with DriverRequest of username "JonhDoe" and phone "+375484848484"
    Then UsernameAlreadyExistsException should be thrown for username "JonhDoe" and phone "+375484848484"
  Scenario: Create Driver by not existing username and existing phone
    Given Driver with not existing username "viktordiktor" and existing phone "+375111111111"
    When createDriver method is called with DriverRequest of username "375111111111" and phone "+375111111111"
    Then PhoneAlreadyExistsException should be thrown for username "375111111111" and phone "+375111111111"

  Scenario: Edit Driver by existing ID and not existing username and not existing phone
    Given Driver with ID 3 exists and Driver with username "USERNAME2" and phone "+375222222222" not exists
    When editDriver method is called with ID 3 and DriverRequest of username "USERNAME2" and phone "+375222222222"
    Then DriverResponse should contains driver with ID 3 and username "USERNAME2" and phone "+375222222222"
  Scenario: Edit Driver by existing ID and existing username and not existing phone
    Given Driver with ID 3 exists and Driver with existing username "JonhDoe" and not existing phone "+375222222222"
    When editDriver method is called with ID 3 and DriverRequest of username "JonhDoe" and phone "+375222222222"
    Then UsernameAlreadyExistsException should be thrown for username "JonhDoe" and phone "+375484848484"
  Scenario: Edit Driver by existing ID and not existing username and existing phone
    Given Driver with ID 3 exists and Driver with not existing username "USERNAME2" and existing phone "+375111111111"
    When editDriver method is called with ID 3 and DriverRequest of username "USERNAME2" and phone "+375111111111"
    Then PhoneAlreadyExistsException should be thrown for username "USERNAME2" and phone "+375111111111"
  Scenario: Edit Driver by not existing ID and not existing username and not existing phone
    Given Driver with ID 999 not exists and Driver with username "USERNAME2" and phone "+375222222222" not exists
    When editDriver method is called with ID 999 and DriverRequest of username "USERNAME2" and phone "+375222222222"
    Then DriverNotFoundException should be thrown for ID 999

  Scenario: Delete Driver by existing ID
    Given Driver with ID 1 exists
    When deleteDriver method is called with ID 1
    Then Should return No Content for ID 1 and delete Driver
  Scenario: Delete Driver by not existing ID
    Given Driver with ID 999 not exists
    When deleteDriver method is called with ID 999
    Then DriverNotFoundException should be thrown for ID 999

  Scenario: Accept Ride with Ride ID and existing available Driver ID
    Given Driver with ID 1 exists and available and Ride ID "ride1"
    When acceptRide method is called with Driver ID 1 and Ride ID "ride1"
    Then Should return No Content And send request to Ride Service for Driver ID 1 and Ride ID "ride1"
    And Driver should become not available
  Scenario: Accept Ride with Ride ID and existing not available Driver ID
    Given Driver with ID 2 exists and not available and Ride ID "ride1"
    When acceptRide method is called with Driver ID 2 and Ride ID "ride1"
    Then DriverIsNotAvailableException should be thrown for ID 2
  Scenario: Accept Ride with Ride ID and not existing Driver ID
    Given Driver with ID 999 not exists
    When acceptRide method is called with Driver ID 999 and Ride ID "ride1"
    Then DriverNotFoundException should be thrown for ID 999

  Scenario: Reject Ride with Ride ID and existing not available Driver ID
    Given Driver with ID 1 exists and not available and Ride ID "ride1"
    When rejectRide method is called with Driver ID 1 and Ride ID "ride1"
    Then Should return No Content And send request to Ride Service for Driver ID 1 and Ride ID "ride1"
    And Driver should become available
  Scenario: Reject Ride with Ride ID and existing available Driver ID
    Given Driver with ID 1 exists and available and Ride ID "ride1"
    When rejectRide method is called with Driver ID 1 and Ride ID "ride1"
    Then DriverNoRidesException should be thrown for ID 999
  Scenario: Reject Ride with Ride ID and not existing available Driver ID
    Given Driver with ID 999 not exists
    When rejectRide method is called with Driver ID 999 and Ride ID "ride1"
    Then DriverNotFoundException should be thrown for ID 999

  Scenario: Start Ride with Ride ID and existing not available Driver ID
    Given Driver with ID 1 exists and not available and Ride ID "ride1"
    When rejectRide method is called with Driver ID 1 and Ride ID "ride1"
    Then Should return No Content And send request to Ride Service for Driver ID 1 and Ride ID "ride1"
  Scenario: Start Ride with Ride ID and existing available Driver ID
    Given Driver with ID 1 exists and available and Ride ID "ride1"
    When rejectRide method is called with Driver ID 1 and Ride ID "ride1"
    Then DriverNoRidesException should be thrown for ID 999
  Scenario: Start Ride with Ride ID and not existing available Driver ID
    Given Driver with ID 999 not exists
    When rejectRide method is called with Driver ID 999 and Ride ID "ride1"
    Then DriverNotFoundException should be thrown for ID 999

  Scenario: Finish Ride with Ride ID and existing not available Driver ID
    Given Driver with ID 1 exists and not available and Ride ID "ride1"
    When finishRide method is called with Driver ID 1 and Ride ID "ride1"
    Then Should return No Content And send request to Ride Service for Driver ID 1 and Ride ID "ride1"
    And Driver should become available
  Scenario: Reject Ride with Ride ID and existing available Driver ID
    Given Driver with ID 1 exists and available and Ride ID "ride1"
    When finishRide method is called with Driver ID 1 and Ride ID "ride1"
    Then DriverNoRidesException should be thrown for ID 999
  Scenario: Reject Ride with Ride ID and not existing available Driver ID
    Given Driver with ID 999 not exists
    When finishRide method is called with Driver ID 999 and Ride ID "ride1"
    Then DriverNotFoundException should be thrown for ID 999

  Scenario: Update Driver rating by existing driver ID and rating and comment
    Given Driver with ID 1 exists and rating 5 and comment "Super :)"
    When createReview method is called with RatingToDriverRequest of driver ID 1 and rating 5 and comment "Super :)"
    Then Should return No Content for ID 1 and rating 5 and comment "Super :)"
  Scenario: Update Driver rating by not existing ID and rating and comment
    Given Driver with ID 999 not exists
    When createReview method is called with RatingToDriverRequest of driver ID 999 and rating 5 and comment "Super :)"
    Then DriverNotFoundException should be thrown for ID 999

  Scenario: Add to Driver by Existing Driver ID new Car
    Given Driver with ID 1 exists and CarRequest of number "1111AA1" and model "Citroen Xsara" and color "White"
    When addCarToDriver method is called with Driver ID 1 and CarRequest of number "1111AA1" and model "Citroen Xsara" and color "White"
    Then DriverResponse should contains driver with ID 1 and CarResponse of number "1111AA1" and model "Citroen Xsara" and color "White"
  Scenario: Add to Driver by Not Existing Driver ID new Car
    Given Driver with ID 999 not exists
    When addCarToDriver method is called with Driver ID 999 and CarRequest of number "1111AA1" and model "Citroen Xsara" and color "White"
    Then DriverNotFoundException should be thrown for ID 999

  Scenario: Get Car by existing ID
    Given Car with ID 1 exists
    When getCarById method is called with ID 1
    Then CarResponse should contains car with ID 1
  Scenario: Get Car by not existing ID
    Given Car with ID 999 not exists
    When getCarById method is called with ID 999
    Then CarNotFoundException should be thrown for ID 999

  Scenario: Edit Сar by existing ID and not existing number
    Given Car with ID 3 exists and Car with number "1111AA1" not exists
    When editCar method is called with ID 3 and CarRequest of number "1111AA1"
    Then CarResponse should contains car with ID 3 and number "1111AA1"
  Scenario: Edit Car by existing ID and existing number
    Given Car with ID 3 exists and Car with existing number "2222AA2"
    When editCar method is called with ID 3 and CarRequest of number "2222AA2"
    Then CarNumberAlreadyExistsException should be thrown for number "2222AA2"
  Scenario: Edit Car by not existing ID and not existing number
    Given Ride with ID 999 not exists and Ride with number "1111AA1" not exists
    When editCar method is called with ID 999 and CarRequest of number "1111AA1"
    Then CarNotFoundException should be thrown for ID 999
