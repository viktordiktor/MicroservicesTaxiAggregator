Feature: Payment Service

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
