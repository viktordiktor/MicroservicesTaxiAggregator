Feature: Passenger Service

  Scenario: Get Passenger by existing ID
    Given Passenger with ID "11111111-1111-1111-1111-111111111111" exists
    When getPassengerById method is called with ID "11111111-1111-1111-1111-111111111111"
    Then PassengerResponse should contains passenger with ID "11111111-1111-1111-1111-111111111111"
  Scenario: Get Passenger by not existing ID
    Given Passenger with ID "99999999-9999-9999-9999-999999999999" not exists
    When getPassengerById method is called with ID "99999999-9999-9999-9999-999999999999"
    Then PassengerNotFoundException should be thrown for ID "99999999-9999-9999-9999-999999999999"

  Scenario: Create Passenger by not existing username and not existing phone
    Given Passenger with username "viktordiktor" and phone "+375484848484" not exists
    When createPassenger method is called with PassengerRequest of username "viktordiktor" and phone "+375484848484"
    Then PassengerResponse should contains passenger with username "viktordiktor" and phone "+375484848484"
  Scenario: Create Passenger by existing username and not existing phone
    Given Passenger with existing username "JonhDoe" and not existing phone "+375484848484"
    When createPassenger method is called with PassengerRequest of username "JonhDoe" and phone "+375484848484"
    Then UsernameAlreadyExistsException should be thrown for username "JonhDoe" and phone "+375484848484"
  Scenario: Create Passenger by not existing username and existing phone
    Given Passenger with not existing username "viktordiktor" and existing phone "+375111111111"
    When createPassenger method is called with PassengerRequest of username "375111111111" and phone "+375111111111"
    Then PhoneAlreadyExistsException should be thrown for username "375111111111" and phone "+375111111111"

  Scenario: Edit Passenger by existing ID and not existing username and not existing phone
    Given Passenger with ID "33333333-3333-3333-3333-333333333333" exists and Passenger with username "USERNAME2" and phone "+375222222222" not exists
    When editPassenger method is called with ID "33333333-3333-3333-3333-333333333333" and PassengerRequest of username "USERNAME2" and phone "+375222222222"
    Then PassengerResponse should contains passenger with ID "33333333-3333-3333-3333-333333333333" and username "USERNAME2" and phone "+375222222222"
  Scenario: Edit Passenger by existing ID and existing username and not existing phone
    Given Passenger with ID "33333333-3333-3333-3333-333333333333" exists and Passenger with existing username "JonhDoe" and not existing phone "+375222222222"
    When editPassenger method is called with ID "33333333-3333-3333-3333-333333333333" and PassengerRequest of username "JonhDoe" and phone "+375222222222"
    Then UsernameAlreadyExistsException should be thrown for username "JonhDoe" and phone "+375484848484"
  Scenario: Edit Passenger by existing ID and not existing username and existing phone
    Given Passenger with ID "33333333-3333-3333-3333-333333333333" exists and Passenger with not existing username "USERNAME2" and existing phone "+375111111111"
    When editPassenger method is called with ID "33333333-3333-3333-3333-333333333333" and PassengerRequest of username "USERNAME2" and phone "+375111111111"
    Then PhoneAlreadyExistsException should be thrown for username "USERNAME2" and phone "+375111111111"
  Scenario: Edit Passenger by not existing ID and not existing username and not existing phone
    Given Passenger with ID "99999999-9999-9999-9999-999999999999" not exists and Passenger with username "USERNAME2" and phone "+375222222222" not exists
    When editPassenger method is called with ID "99999999-9999-9999-9999-999999999999" and PassengerRequest of username "USERNAME2" and phone "+375222222222"
    Then PassengerNotFoundException should be thrown for ID "99999999-9999-9999-9999-999999999999"

  Scenario: Delete Passenger by existing ID
    Given Passenger with ID "11111111-1111-1111-1111-111111111111" exists
    When deletePassenger method is called with ID "11111111-1111-1111-1111-111111111111"
    Then Should return No Content for ID "11111111-1111-1111-1111-111111111111" and delete Passenger
  Scenario: Delete Passenger by not existing ID
    Given Passenger with ID "99999999-9999-9999-9999-999999999999" not exists
    When deletePassenger method is called with ID "99999999-9999-9999-9999-999999999999"
    Then PassengerNotFoundException should be thrown for ID "99999999-9999-9999-9999-999999999999"
    
  Scenario: Update Passenger rating by existing passenger ID and rating and comment
    Given Passenger with ID "11111111-1111-1111-1111-111111111111" exists and rating 5 and comment "Super :)"
    When createReview method is called with RatingToPassengerRequest of passenger ID "11111111-1111-1111-1111-111111111111" and rating 5 and comment "Super :)"
    Then Should return No Content for ID "11111111-1111-1111-1111-111111111111" and rating 5 and comment "Super :)"
  Scenario: Update Passenger rating by not existing ID and rating and comment
    Given Passenger with ID "99999999-9999-9999-9999-999999999999" not exists and rating 5 and comment "Super :)"
    When createReview method is called with RatingToPassengerRequest of passenger ID "99999999-9999-9999-9999-999999999999" and rating 5 and comment "Super :)"
    Then PassengerNotFoundException should be thrown for ID "99999999-9999-9999-9999-999999999999"

  Scenario: Create Customer by existing passenger ID and username and phone and amount
    Given Passenger with ID "11111111-1111-1111-1111-111111111111" exists and CustomerDataRequest of username "viktordiktor" and phone "+375111111111" and amount "20.52"
    When createCustomerByPassenger method is called with Passenger ID "11111111-1111-1111-1111-111111111111" and CustomerDataRequest of username "viktordiktor" and phone "+375111111111" and amount "20.52"
    Then Should return No Content and send Customer Creation Request for Passenger ID "11111111-1111-1111-1111-111111111111" and username "viktordiktor" and phone "+375111111111" and amount "20.52"
  Scenario: Create Customer by existing passenger ID and username and phone and amount
    Given Passenger with ID "99999999-9999-9999-9999-999999999999" not exists and CustomerDataRequest of username "viktordiktor" and phone "+375111111111" and amount "20.52"
    When createCustomerByPassenger method is called with Passenger ID "99999999-9999-9999-9999-999999999999" and CustomerDataRequest of username "viktordiktor" and phone "+375111111111" and amount "20.52"
    Then PassengerNotFoundException should be thrown for ID "99999999-9999-9999-9999-999999999999"





