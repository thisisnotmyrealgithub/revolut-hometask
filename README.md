# Revolut test task

Basic money transfer service. Works with 2 digits after point (BigDecimal). 

Tech stack:

5 J, 1 G :) 
- java 11
- jetty
- jersey
- jackson 
- junit
- gradle

### How to build and start app

Easy: 

```./gradlew build && java -jar build/libs/revolut-test-task-all.jar```

### decimal precision 

2 decimal places

rounding starting from the third character starts down (all characters after 2 are ignored)

### Functions 

Important notice: amount can be only positive. If you put negative or zero amount (except create account), you will receive code 400.
When you create account amount can be zero.

If you try operate with nonexistent account, you will receive code 404. 

- Create account with amount (return account id and amount): 

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
- might be for crazy big amounts smth like TooRichException :)
