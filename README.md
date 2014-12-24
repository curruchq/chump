# Chump

## Usage
1. Download and update `chump.properties` then place in same directory as jar.
2. Run `java -jar chump-0.0.1-SNAPSHOT.jar`

## REST
### Create a number
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/number -d \
    '{
        "number": "6494297021",
        "domain":"conversant.co.nz",
        "priceListVersionId":1000000,
        "countryId":"147",
        "countryCode":"64",
        "areaCode":"9429",
        "areaCodeDescription": "Auckland - Red Beach Test",
        "freeMinutes": "10",
        "perMinuteCharge": "0",
        "businessPartnerId": 1000076,
        "setupCost": "1",
        "monthlyCharge": "2",
        "currencyId": 121
    }'

### Provision a number
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/number/6494297021/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076, 
        "businessPartnerLocationId": 1000014, 
        "startDate": "2015-02-01"
    }'
    
### Provision an order
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/order/80088/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz"
    }'