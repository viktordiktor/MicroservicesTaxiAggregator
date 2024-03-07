Feature: Ride Service

  Scenario: Get Ride by existing ID
    Given Ride with ID "ride1" exists
    When getRideById method is called with ID "ride1"
    Then RideResponse should contains Ride with ID "ride1"
  Scenario: Get Ride by not existing ID
    Given Ride with ID "ride999" not exists
    When getRideById method is called with ID "ride999"
    Then RideNotFoundException should be thrown for Ride ID "ride999"

  Scenario: Create Ride by Card successful payment
    Given CreateRideRequest by card of Passenger ID 1 and Start Address "addr1" and End Address "addr2" and success Charge ID "chrg1"
    When createRide method is called with Passenger ID 1 and Start Address "addr1" and End Address "addr2" and Charge ID "chrg1"
    Then RideResponse should contains Passenger ID 1 and Start Address "addr1" and End Address "addr2" and Charge ID "chrg1"
  Scenario: Create Ride by Card unsuccessful payment
    Given CreateRideRequest by card of Passenger ID 1 and Start Address "addr1" and End Address "addr2" and unsuccessful Charge ID "chrg999"
    When createRide method is called with Passenger ID 1 and Start Address "addr1" and End Address "addr2" and Charge ID "chrg999"
    Then ChargeIsNotSuccessException should be thrown for Charge ID "chrg999"
  Scenario: Create Ride by Cash
    Given CreateRideRequest by cash of Passenger ID 1 and Start Address "addr1" and End Address "addr2"
    When createRide method is called with Passenger ID 1 and Start Address "addr1" and End Address "addr2"
    Then RideResponse should contains Passenger ID 1 and Start Address "addr1" and End Address "addr2"

  Scenario: Close Ride created by Card and return Customer's charge
    Given Ride with ID "ride1" exists and opened by card
    When closeRide method is called with Ride ID "ride1"
    Then CloseRideResponse should contains Card Payment Method and CustomerChargeReturnResponse should not be null for Ride ID "ride1"
  Scenario: Close Ride created by Cash
    Given Ride with ID "ride2" exists and opened by cash
    When closeRide method is called with Ride ID "ride2"
    Then CloseRideResponse should contains Cash Payment Method for Ride ID "ride2"
  Scenario: Close not opened Ride
    Given Ride with ID "ride3" exists and not opened
    When closeRide method is called with Ride ID "ride3"
    Then RideIsNotOpenedException should be thrown for Ride ID "ride3"
  Scenario: Close not existing Ride
    Given Ride with ID "ride999" not exists
    When closeRide method is called with Ride ID "ride999"
    Then RideNotFoundException should be thrown for Ride ID "ride999"
    
  Scenario: Change Ride status with valid action and existing Ride ID
    Given ChangeRideRequest with Action "ACCEPT" and existing Ride ID "ride1"
    When changeRideStatus is called with Action "ACCEPT" and Ride ID "ride1"
    Then Ride with ID "ride1" should change status to ACCEPTED
  Scenario: Change Ride status with invalid action and existing Ride ID
    Given ChangeRideRequest with Action "FINISH" and existing Ride ID "ride2"
    When changeRideStatus is called with Action "FINISH" and Ride ID "ride2"
    Then RideIsNotStartedException should be thrown for Ride ID "ride2"
  Scenario: Change Ride status with not existing Ride ID
    Given Ride with ID "ride999" not exists
    When changeRideStatus is called with Action "ACCEPT" and Ride ID "ride999"
    Then RideNotFoundException should be thrown for Ride ID "ride999"