# Usage
1. Clone `git clone git@bitbucket.org:conversantltd/chump.git && cd chump`
2. Build `mvn clean install`
3. Configure `chump.properties`
3. Run `java -jar target/chump-0.0.1-SNAPSHOT.jar`

# REST
## Create a number
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/number -d \
    '{
        "number": "6494297021",
        "domain": "conversant.co.nz",
        "priceListVersionId": 1000000,
        "countryId": "147",
        "countryCode": "64",
        "areaCode": "9429",
        "areaCodeDescription": "Auckland - Red Beach Test",
        "freeMinutes": "10",
        "perMinuteCharge": "0",
        "businessPartnerId": 1000076,
        "setupCost": "1",
        "monthlyCharge": "2",
        "currencyId": 121
    }'

## Provision a number
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/number/6494297021/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076, 
        "businessPartnerLocationId": 1000014, 
        "startDate": "2015-02-01"
    }'
    
## Provision an order
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/order/80088/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz"
    }'

## Search call records
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/callrecord/search -d \
    '{
        "ids": ["22985c454b0977543912753e5d1d3ff0@202.180.76.164", "6cc1347370082741616f98dc286a30e6@conversant.co.nz"]
    }'
