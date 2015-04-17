# Usage
1. Clone `git clone git@bitbucket.org:conversantltd/chump.git && cd chump`
2. Build `mvn clean install`
3. Configure `chump.properties`
3. Run `java -jar target/chump-0.0.1-SNAPSHOT.jar`

# REST - v2
## Create a number
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers -d \
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
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers/6494297021/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076,
        "businessPartnerLocationId": 1000014,
        "startDate": "2015-02-01",
        "paidUntilDate": "2015-03-01"
    }'

## Provision an order
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/orders/80088/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz"
    }'

# REST - v1
## Create a number
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers -d \
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
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers/6494297021/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076, 
        "businessPartnerLocationId": 1000014, 
        "startDate": "2015-02-01",
        "paidUntilDate": "2015-03-01"
    }'
    
## Provision an order
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/orders/80088/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz"
    }'

## Read order lines (optional query parameters - productId and productCategoryId)
    curl http://localhost:9090/chump/v1/orders/1002217/lines

## Search call records
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/callrecords/search -d \
    '{
        "ids": ["22985c454b0977543912753e5d1d3ff0@202.180.76.164", "6cc1347370082741616f98dc286a30e6@conversant.co.nz"]
    }'

## Get a list of locations for a business partner
    curl http://localhost:9090/chump/v1/businesspartners/1000009/locations

## Get invoices by business partner search key
    curl http://localhost:9090/chump/v1/invoices?businessPartnerSearchKey=1000009

## Get invoice lines by id
    curl http://localhost:9090/chump/v1/invoices/1000060/lines

## Get subscriptions by business partner search key
    curl http://localhost:9090/chump/v1/subscriptions?businessPartnerSearchKey=1000009

## Get subscription by id
    curl http://localhost:9090/chump/v1/subscriptions/1000619

## Get products by category id
    curl http://localhost:9090/chump/v1/products?productCategoryId=1000022

## Get product by product id
    curl http://localhost:9090/chump/v1/products/1015750

## Get product price for business partner
    curl http://localhost:9090/chump/v1/products/1015750/price?businessPartnerSearchKey=1000009

## Migrate a customer
    curl -X POST -H "Content-type: application/json" http://localhost:9090/chump/v1/migration -d \
    '{
        "businessPartnerSearchKey": "1000009",
        "realm": "1000855",
        "mainNumber": "6494297021",
        "numbers": [
            "6494297021", "6494295000"
        ],
        "priceListVersionId": 1000000,
        "businessPartnerLocationId": 1000014
    }'

## Update subscription
    curl -XPOST -H "Content-Type: application/json" http://localhost:9090/chump/v1/subscriptions/1000619 -d \
    '{
        "name" : "+12124016222",
        "businessPartnerId" : 1000022,
        "businessPartnerLocationId" : 1000023,
        "productId" : 1000740,
        "subscriptionTypeId" : 1000004,
        "startDate" : "2015-04-01",
        "paidUntilDate" : "2015-04-30",
        "renewalDate" : "2015-04-30",
        "billInAdvance" : true,
        "qty" : 1,
        "userId" : 0
    }'

## Create a new subscription
    curl -XPOST -H "Content-Type: application/json" http://localhost:9090/chump/v1/subscriptions -d \
    '{
        "name" : "+12121234567",
        "businessPartnerId" : 1000022,
        "businessPartnerLocationId" : 1000023,
        "productId" : 1000740,
        "subscriptionTypeId" : 1000004,
        "startDate" : "2015-05-01",
        "paidUntilDate" : "2015-05-30",
        "renewalDate" : "2015-05-30",
        "billInAdvance" : true,
        "qty" : 1,
        "userId" : 0
    }'
