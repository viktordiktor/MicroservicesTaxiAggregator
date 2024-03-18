Feature: Payment Service

  Scenario: Create Customer that not exists by Passenger
    Given Customer not exists with Username "viktordiktor" and phone "+375292547777" and Passenger ID "11111111-1111-1111-1111-111111111111" and Amount "9.9"
    When createCustomer method is called with Username "viktordiktor" and phone "+375292547777" and Passenger ID "11111111-1111-1111-1111-111111111111" and Amount "9.9"
    Then CustomerCreationResponse should contains Username "viktordiktor" and Phone "+375292547777"
  Scenario: Create Customer that exists by Passenger
    Given Customer exists with Passenger ID "22222222-2222-2222-2222-222222222222"
    When createCustomer method is called with Username "viktordiktor" and phone "+375292547777" and Passenger ID "22222222-2222-2222-2222-222222222222" and Amount "9.9"
    Then CustomerAlreadyExistsException should be thrown for Customer with Passenger ID "22222222-2222-2222-2222-222222222222"

  Scenario: Create Customer Charge with Enough Funds and Existing Customer
    Given Customer enough funds charge with Amount "50.5" and Passenger ID "11111111-1111-1111-1111-111111111111" and Currency "usd"
    When createCustomerCharge method is called with Amount "50.5" and Passenger ID "11111111-1111-1111-1111-111111111111" and Currency "usd"
    Then CustomerChargeResponse should contains Amount "50.5" and Passenger ID "11111111-1111-1111-1111-111111111111" and Currency "usd"
    And CustomerChargeResponse should be successful for Customer With Passenger ID "11111111-1111-1111-1111-111111111111"
  Scenario: Create Customer Charge with Not Enough Funds and Existing Customer
    Given Customer not enough funds charge with Amount "50.5" and Passenger ID "22222222-2222-2222-2222-222222222222" and Currency "usd"
    When createCustomerCharge method is called with Amount "50.5" and Passenger ID "22222222-2222-2222-2222-222222222222" and Currency "usd"
    Then InsufficientFundsException should be Thrown for Customer With Passenger ID "22222222-2222-2222-2222-222222222222"
  Scenario: Create Customer Charge with not Existing Customer
    Given Customer not exists with Passenger ID "33333333-3333-3333-3333-333333333333"
    When createCustomerCharge method is called with Amount "50.5" and Passenger ID "33333333-3333-3333-3333-333333333333" and Currency "usd"
    Then CustomerNotFoundException should be thrown

  Scenario: Get Customer Charge by Charge ID with existing Customer
    Given Charge ID "charge1" and Customer exists
    When getCustomerCharge method is called with Charge ID "charge1"
    Then CustomerChargeResponse should contains charge with Charge ID "charge1"
  Scenario: Get Customer Charge by Charge ID with not existing Customer
    Given Charge ID "charge2" and Customer not exists
    When getCustomerCharge method is called with Charge ID "charge2"
    Then CustomerNotFoundException should be thrown

  Scenario: Calculate Ride Price With Coupon
    Given Ride Length 9.58 and Ride DateTime "2024-03-02T10:30:00" and Coupon "coupon1"
    When calculateRidePriceMethod is called with Ride Length 9.58 and Ride DateTime "2024-03-02T10:30:00" and Coupon "coupon1"
    Then CustomerCalculateRideResponse should contains Ride Length 9.58 and Ride DateTime "2024-03-02T10:30:00"
    And CustomerCalculateRideResponse should contains Coupon "coupon1"
    And CustomerCalculateRideResponse should contains Not Null Ride Price
  Scenario: Calculate Ride Price Without Coupon
    Given Ride Length 9.58 and Ride DateTime "2024-03-02T10:30:00"
    When calculateRidePriceMethod is called with Ride Length 9.58 and Ride DateTime "2024-03-02T10:30:00" and Coupon "null"
    Then CustomerCalculateRideResponse should contains Ride Length 9.58 and Ride DateTime "2024-03-02T10:30:00"
    And CustomerCalculateRideResponse should contains Not Null Ride Price

  Scenario: Generate Token by Card
    Given CardRequest of Card Number "4242424242424242" and Exp Month 12 and Exp Year 24 and CVC 111
    When generateTokenByCard is called with Card Request of Card Number "4242424242424242" and Exp Month 12 and Exp Year 24 and CVC 111
    Then TokenResponse should contains Token and Card Number "4242424242424242"

  Scenario: Create Charge by Token
    Given ChargeRequest of Token "token1" and currency "USD" and amount "999.412"
    When charge method is called with Token "token1" and currency "USD" and amount "999.412"
    Then ChargeResponse should contains amount "999.412" for token "token1"
    And ChargeResponse should be successful for token "token1"

  Scenario: Get Charge by Charge ID
    Given Charge valid ID "charge1"
    When getChargeById method is called with Charge ID "charge1"
    Then ChargeResponse should contains Charge ID "charge1"
    
  Scenario: Create Coupon
    Given CouponRequest of Month Duration 14 and Percent "94.24"
    When createCoupon method is called with Month Duration 14 and Percent "94.24"
    Then CouponResponse should contains Month Duration 14 and Percent "94.24"
