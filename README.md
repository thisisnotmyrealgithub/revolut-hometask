# Revolut test task

Basic money transfer service. Works with 2 digits after point (BigDecimal). 

Tech stack:

5 J, 1 G :) 
- java
- jetty
- jersey
- jackson 
- junit
- gradle


### Functions 

- Create account with amount: 
curl -X POST http://localhost:8080/api/account -H 'content-type: application/json;charset=UTF-8' --data-binary '{"amount": 100}' --compressed

- Account balance:
curl 'http://localhost:8080/api/account/balance?id=1'

- Add money to account:
curl -X POST 'http://localhost:8080/api/account/add?id=1' -H 'content-type: application/json;charset=UTF-8' --data-binary '{"amount": 100}' --compressed

- Charge money from account:
curl -X POST 'http://localhost:8080/api/account/charge?id=1' -H 'content-type: application/json;charset=UTF-8' --data-binary '{"amount": 100}' --compressed

- Transfer money: 
curl -X POST 'http://localhost:8080/api/account/transfer?from=1&to=2' -H 'content-type: application/json;charset=UTF-8' --data-binary '{"amount": 100}' --compressed

###what can be improved?

- more tests (each case for method (like can't found first account or second account for transfer) )
- adding something for dependency injection (now AccountService just static field in resource, not good, but now enough)
- adding transaction history 