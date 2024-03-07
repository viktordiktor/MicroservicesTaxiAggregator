Feature: End to End Tests

  Scenario: Passenger sends request to Ride Service and get All Rides by him
    Given Passenger with ID 1 exists
    When getRidesByPassengerId is called by Ride Service Client
    Then PageRideResponse should not be null
    And PageRideResponse should contains at least one RideResponse
    And All Ride Responses should contains Passenger ID


  Scenario: Driver sends request to Ride Service and get All Rides by him
    Given Driver with ID 1 exists
    When getRidesByDriverId is called by Ride Service Client
    Then PageRideResponse should not be null
    And PageRideResponse should contains at least one RideResponse
    And All Ride Responses should contains Driver ID


  Scenario: Passenger opening Ride By Card With Customer Not Exists
    Given StartGeo "37.1234,122.5678" and EndGeo "37.1234,123.5678"
    When calculateDistance method is called with CalculateDistanceRequest
    Then CalculateDistanceResponse should contains Not Null Distance
    And CalculateDistanceResponse should not contains Error Message

    Given Ride Length 9.99 and Ride DateTime "2024-03-02T10:30:00" and Valid Coupon "TGdq2KcD"
    When calculateRidePrice method is called with CalculateRideRequest
    Then CustomerCalculateRideResponse should contains Not Null Price
    And CustomerCalculateRideResponse should not contains Error Message

    Given Passenger with ID 5 exists
    When isCustomerExists method is called by Payment Service Client
    Then CustomerExistsResponse should be False


  Scenario: Create Customer by Passenger
    Given Username "testuser" and phone "+375292547787" and Existing Passenger ID 2 and Amount "95000.2"
    When sendCustomerCreationRequest method is called with CustomerCreationRequest
    Then CustomerExistsResponse should be True after 10 seconds


  Scenario: Passenger opening Ride By Card With Customer Exists and Then Close It
    Given StartGeo "37.1234,122.5678" and EndGeo "37.1234,123.5678"
    When calculateDistance method is called with CalculateDistanceRequest
    Then CalculateDistanceResponse should contains Not Null Distance
    And CalculateDistanceResponse should not contains Error Message

    Given Ride Length 9.99 and Ride DateTime "2024-03-02T10:30:00" and Valid Coupon "TGdq2KcD"
    When calculateRidePrice method is called with CalculateRideRequest
    Then CustomerCalculateRideResponse should contains Not Null Price
    And CustomerCalculateRideResponse should not contains Error Message

    Given Passenger with ID 2 exists
    When isCustomerExists method is called by Payment Service Client
    Then CustomerExistsResponse should be True

    Given Valid CustomerChargeRequest of Currency "usd"
    When customerCharge method is called with customerChargeRequest
    Then Customer Charge should have Not Null ID
    And Customer Charge should have True success
    And Customer Charge should Not have Error Message

    Given Valid CreateRideRequest by Card of Start Address "address1" and End Address "address2"
    When createRideRequest method is called with createRideRequest
    Then Ride Response should contains Not Null ID
    And Ride Response should Not have Error Message
    And Ride Response should have By Card Payment method

    When closeRide method is called with Ride ID
    Then CloseRideResponse should Card Payment Method
    And CloseRideResponse should contains valid CustomerChargeReturnResponse

  Scenario: Passenger opening Ride By Cash and then Close It
    Given StartGeo "37.1234,122.5678" and EndGeo "37.1234,123.5678"
    When calculateDistance method is called with CalculateDistanceRequest
    Then CalculateDistanceResponse should contains Not Null Distance
    And CalculateDistanceResponse should not contains Error Message

    Given Ride Length 9.99 and Ride DateTime "2024-03-02T10:30:00" and Valid Coupon "TGdq2KcD"
    When calculateRidePrice method is called with CalculateRideRequest
    Then CustomerCalculateRideResponse should contains Not Null Price
    And CustomerCalculateRideResponse should not contains Error Message

    Given Valid CreateRideRequest by Cash of Start Address "address1" and End Address "address2" and Passenger with ID 3
    When createRideRequest method is called with createRideRequest
    Then Ride Response should contains Not Null ID
    And Ride Response should Not have Error Message
    And Ride Response should have By Cash Payment method

    When closeRide method is called with Ride ID
    Then CloseRideResponse should Cash Payment Method
    And CloseRideResponse should not contains CustomerChargeReturnResponse

  Scenario: Passenger opening Ride by Cash and then Driver accept, start and finish Ride, then Driver sends Review to Passenger, Passenger sends Review to Driver
    Given StartGeo "37.1234,122.5678" and EndGeo "37.1234,123.5678"
    When calculateDistance method is called with CalculateDistanceRequest
    Then CalculateDistanceResponse should contains Not Null Distance
    And CalculateDistanceResponse should not contains Error Message

    Given Ride Length 9.99 and Ride DateTime "2024-03-02T10:30:00" and Valid Coupon "TGdq2KcD"
    When calculateRidePrice method is called with CalculateRideRequest
    Then CustomerCalculateRideResponse should contains Not Null Price
    And CustomerCalculateRideResponse should not contains Error Message

    Given Valid CreateRideRequest by Cash of Start Address "address1" and End Address "address2" and Passenger with ID 3
    When createRideRequest method is called with createRideRequest
    Then Ride Response should contains Not Null ID
    And Ride Response should Not have Error Message
    And Ride Response should have By Cash Payment method

    Given Driver with ID 1 exists
    When sendChangeRideStatusRequest method is called with Ride Action "ACCEPT"
    Then Ride should change status to "ACCEPTED"
    When sendChangeRideStatusRequest method is called with Ride Action "START"
    Then Ride should change status to "STARTED"
    When sendChangeRideStatusRequest method is called with Ride Action "FINISH"
    Then Ride should change status to "FINISHED"

    Given Passenger with ID 3 exists
    When sendRatingDriverRequest method is called with Rating 5 and Comment "Cool"
    Then Driver should contains new Review

    Given Driver with ID 1 exists
    When sendRatingPassengerRequest method is called with Rating 5 and Comment "Cool"
    Then Passenger should contains new Review


  Scenario: Passenger create customer and then not have enough balance to create Ride
    Given Username "pooruser" and phone "+37529555555" and Existing Passenger ID 3 and Amount "0.01"
    When sendCustomerCreationRequest method is called with CustomerCreationRequest
    Then CustomerExistsResponse should be True after 10 seconds

    Given StartGeo "37.1234,122.5678" and EndGeo "37.1234,123.5678"
    When calculateDistance method is called with CalculateDistanceRequest
    Then CalculateDistanceResponse should contains Not Null Distance
    And CalculateDistanceResponse should not contains Error Message

    Given Ride Length 9.99 and Ride DateTime "2024-03-02T10:30:00" and Valid Coupon "TGdq2KcD"
    When calculateRidePrice method is called with CalculateRideRequest
    Then CustomerCalculateRideResponse should contains Not Null Price
    And CustomerCalculateRideResponse should not contains Error Message

    Given Passenger with ID 3 exists
    When isCustomerExists method is called by Payment Service Client
    Then CustomerExistsResponse should be True

    Given Valid CustomerChargeRequest of Currency "usd"
    When customerCharge method is called with customerChargeRequest
    Then FeignClientException should be Thrown with message "Insufficient funds!"



