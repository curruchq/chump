# Usage
1. Clone `git clone git@bitbucket.org:conversantltd/chump.git && cd chump`
2. Build `mvn clean install`
3. Configure `chump.properties`
3. Run `java -jar target/chump-0.0.1-SNAPSHOT.jar`

# REST - v2
## Create a number (optional - filter.exclude.did)
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

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers -d \
    '{
        "numbers": ["6494297022", "6494297023"],
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

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers -d \
    '{
        "requests": [
            {
                "number": "6494297024",
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
            },
            {
                "number": "6494297025",
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
            }
        ]
    }'

## Provision a number (optional - filter.exclude.did and filter.exclude.call)
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers/6494297021/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076,
        "businessPartnerLocationId": 1000014,
        "startDate": "2015-02-01",
        "paidUntilDate": "2015-03-01"
    }'

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers/provision -d \
    '{
        "numbers": ["6494297022", "6494297023"],
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076,
        "businessPartnerLocationId": 1000014,
        "startDate": "2015-02-01",
        "paidUntilDate": "2015-03-01"
    }'

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers/6494297024/provision -d \
    '{
        "requests": [
            {
                "number": "6494297024",
                "realm": "conversant.co.nz",
                "proxy": "c-vm-02.conversant.co.nz",
                "businessPartnerId": 1000076,
                "businessPartnerLocationId": 1000014,
                "startDate": "2015-02-01",
                "paidUntilDate": "2015-03-01"
            },
            {
                "number": "6494297025",
                "realm": "conversant.co.nz",
                "proxy": "c-vm-02.conversant.co.nz",
                "businessPartnerId": 1000076,
                "businessPartnerLocationId": 1000014,
                "startDate": "2015-02-01",
                "paidUntilDate": "2015-03-01"
            }
        ]
    }'

## Subscribe a number (optional - filter.exclude.did)
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers/6494297021/subscribe -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076,
        "businessPartnerLocationId": 1000014,
        "startDate": "2015-02-01",
        "paidUntilDate": "2015-03-01"
    }'

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers/subscribe -d \
    '{
        "numbers": ["6494297022", "6494297023"],
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076,
        "businessPartnerLocationId": 1000014,
        "startDate": "2015-02-01",
        "paidUntilDate": "2015-03-01"
    }'

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers/6494297024/subscribe -d \
    '{
        "requests": [
            {
                "number": "6494297024",
                "realm": "conversant.co.nz",
                "proxy": "c-vm-02.conversant.co.nz",
                "businessPartnerId": 1000076,
                "businessPartnerLocationId": 1000014,
                "startDate": "2015-02-01",
                "paidUntilDate": "2015-03-01"
            },
            {
                "number": "6494297025",
                "realm": "conversant.co.nz",
                "proxy": "c-vm-02.conversant.co.nz",
                "businessPartnerId": 1000076,
                "businessPartnerLocationId": 1000014,
                "startDate": "2015-02-01",
                "paidUntilDate": "2015-03-01"
            }
        ]
    }'

## Set number as primary caller id
    curl -XPUT -H "Content-type: application/json" http://localhost:9090/chump/v2/numbers/6494297026/callerId -d \
    '{
        "businessPartnerId": 1000076,
        "realm": "conversant.co.nz",
        "startDate": "2015-07-06"
    }'

## Provision an order (optional - filter.exclude.did and filter.exclude.call)
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v2/orders/80088/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz"
    }'

# REST - v1
## Get status
    curl http://localhost:9090/chump/v1/status

## Create a number (optional - filter.exclude.did)
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

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers -d \
    '{
        "numbers": ["6494297022", "6494297023"],
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

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers -d \
    '{
        "requests": [
            {
                "number": "6494297024",
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
            },
            {
                "number": "6494297025",
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
            }
        ]
    }'

## Provision a number (optional - filter.exclude.did and filter.exclude.call)
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers/6494297021/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076, 
        "businessPartnerLocationId": 1000014, 
        "startDate": "2015-02-01",
        "paidUntilDate": "2015-03-01"
    }'

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers/provision -d \
    '{
        "numbers": ["6494297022", "6494297023"],
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076,
        "businessPartnerLocationId": 1000014,
        "startDate": "2015-02-01",
        "paidUntilDate": "2015-03-01"
    }'

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers/6494297024/provision -d \
    '{
        "requests": [
            {
                "number": "6494297024",
                "realm": "conversant.co.nz",
                "proxy": "c-vm-02.conversant.co.nz",
                "businessPartnerId": 1000076,
                "businessPartnerLocationId": 1000014,
                "startDate": "2015-02-01",
                "paidUntilDate": "2015-03-01"
            },
            {
                "number": "6494297025",
                "realm": "conversant.co.nz",
                "proxy": "c-vm-02.conversant.co.nz",
                "businessPartnerId": 1000076,
                "businessPartnerLocationId": 1000014,
                "startDate": "2015-02-01",
                "paidUntilDate": "2015-03-01"
            }
        ]
    }'

## Subscribe a number (optional - filter.exclude.did)
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers/6494297021/subscribe -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076,
        "businessPartnerLocationId": 1000014,
        "startDate": "2015-02-01",
        "paidUntilDate": "2015-03-01"
    }'

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers/subscribe -d \
    '{
        "numbers: ["6494297022", "6494297023"],
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz",
        "businessPartnerId": 1000076,
        "businessPartnerLocationId": 1000014,
        "startDate": "2015-02-01",
        "paidUntilDate": "2015-03-01"
    }'

    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers/6494297022/subscribe -d \
    '{
        "requests": [
            {
                "number": "6494297024",
                "realm": "conversant.co.nz",
                "proxy": "c-vm-02.conversant.co.nz",
                "businessPartnerId": 1000076,
                "businessPartnerLocationId": 1000014,
                "startDate": "2015-02-01",
                "paidUntilDate": "2015-03-01"
            },
            {
                "number": "6494297025",
                "realm": "conversant.co.nz",
                "proxy": "c-vm-02.conversant.co.nz",
                "businessPartnerId": 1000076,
                "businessPartnerLocationId": 1000014,
                "startDate": "2015-02-01",
                "paidUntilDate": "2015-03-01"
            }
        ]
    }'

## Set number as primary caller id
    curl -XPUT -H "Content-type: application/json" http://localhost:9090/chump/v1/numbers/6494297026/callerId -d \
    '{
        "businessPartnerId": 1000076,
        "startDate": "2015-07-06"
    }'

## Provision an order (optional - filter.exclude.did and filter.exclude.call)
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/orders/80088/provision -d \
    '{
        "realm": "conversant.co.nz",
        "proxy": "c-vm-02.conversant.co.nz"
    }'

## Read order lines (optional query parameters - productId and productCategoryId)
    curl http://localhost:9090/chump/v1/orders/52732/lines

## Search call records
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/callrecords/search -d \
    '{
        "ids": ["22985c454b0977543912753e5d1d3ff0@202.180.76.164", "6cc1347370082741616f98dc286a30e6@conversant.co.nz"]
    }'

## Get a list of locations for a business partner
    curl http://localhost:9090/chump/v1/businesspartners/1000009/locations

## Get a list of users for a business partner
    curl http://localhost:9090/chump/v1/businesspartners/1000009/users

## Create a business partner location
    curl -X POST -H "Content-type: application/json" http://localhost:9090/chump/v1/businesspartners/1000009/locations -d \
    '{
        "name": "Warehouse",
        "address1": "1 Storage Lane",
        "address2": "Cargo Estate",
        "address3": "",
        "address4": "",
        "city": "Hanger",
        "zip": "1109",
        "countryId": 262
    }'

## Update a business partner location
    curl -X PUT -H "Content-type: application/json" http://localhost:9090/chump/v1/businesspartners/1000009/locations/1000350 -d \
    '{
        "name" : "John's Office",
        "address1" : "1 Foo Road",
        "city": "Bar City",
        "countryId": 117
    }'

## Get subscribed numbers
    curl http://localhost:9090/chump/v1/businesspartners/1000009/numbers

## Get invoices by business partner search key
    curl http://localhost:9090/chump/v1/invoices?businessPartnerSearchKey=1000009

## Get invoice lines by id
    curl http://localhost:9090/chump/v1/invoices/1000060/lines

## Get invoice radius accounts
    curl http://localhost:9090/chump/v1/invoices/1000060/radiusAccounts   

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
    curl -XPUT -H "Content-Type: application/json" http://localhost:9090/chump/v1/subscriptions/1000619 -d \
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
        "userId" : 0,
        "orgId": 1000001
    }'

## Get organisation by id
    curl http://localhost:9090/chump/v1/organisations/1000008

## Create user
    curl -XPOST -H "Content-Type: application/json" http://localhost:9090/chump/v1/users -d \
    '{
        "searchKey" : "test",
        "name" : "test",
        "password" : "ra1@",
        "email" : "test123@gmail.com",
        "phone" : "9000000000",
        "mobile" : "9000000000",
        "businessPartnerId" : "1000175",
        "businessPartnerLocationId" : "1000198"
     }'

## Read user
    curl http://localhost:9090/chump/v1/users/1003400

## Update user
    curl -XPUT -H "Content-Type: application/json" http://localhost:9090/chump/v1/users/1003400 -d \
    '{
	    "searchKey" : "testconversant",
	    "name" : "testconversant",
	    "password" : "test",
	    "email" : "test@gmail.com",
	    "phone" : "9010006723"
    }'

## Delete user
    curl -XDELETE http://localhost:9090/chump/v1/users/1003400

## Create user role
    curl -XPOST -H "Content-Type: application/json" http://localhost:9090/chump/v1/users/1001257/roles -d \
    '{
		   "roleId": 1000000
	 }'

## Read user role
    curl http://localhost:9090/chump/v1/users/1001257/roles/1000017

## Delete user role
    curl -XDELETE http://localhost:9090/chump/v1/users/1001257/roles/1000017

## Create usage rating - Inbound billing customer
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/usageratings/billingcustomers -d \
    '{
        "subscriber": "1000009@conversant.co.nz",
        "profileName": "BS_X_1",
        "profileNameAlt": "Int_X_1",
        "timezone": "Pacific/Auckland"
    }'

## Create usage rating - Outbound billing customer
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/usageratings/billingcustomers -d \
    '{
        "domain": "conversant.co.nz",
        "profileName": "BS_X_1",
        "profileNameAlt": "Int_X_1",
        "timezone": "Pacific/Auckland"
    }'

## Update usage rating - Inbound billing customer
    curl -XPUT -H "Content-type: application/json" http://localhost:9090/chump/v1/usageratings/billingcustomers -d \
    '{
        "subscriber": "1000009@conversant.co.nz",
        "profileName": "BS_X_111",
        "profileNameAlt": "Int_X_111",
        "timezone": "Pacific/Honolulu"
    }'

## Update usage rating - Outbound billing customer
    curl -XPUT -H "Content-type: application/json" http://localhost:9090/chump/v1/usageratings/billingcustomers -d \
    '{
        "domain": "conversant.co.nz",
        "profileName": "BS_X_111",
        "profileNameAlt": "Int_X_111",
        "timezone": "Pacific/Honolulu"
    }'
    
## Create business partner (optional searchKey parameter, if not supplied will be generated by ADempiere)
    curl -XPOST -H "Content-type: application/json" http://localhost:9090/chump/v1/businesspartners -d \
    '{
        "orgId": 1000001,
        "name" : "testname",
        "taxExempt" : "true",
        "businessPartnerGroupId" : 1000013
    }'
    
## Read business partner
    curl http://localhost:9090/chump/v1/businesspartners/1000009
    
## Read business partner by group id
    curl http://localhost:9090/chump/v1/businesspartners?businessPartnerGroupId=1000004
    
## Update business partner (optional searchKey parameter, if not supplied will be generated by ADempiere)
    curl -XPUT http://localhost:9090/chump/v1/businesspartners/1000009 -d \
   '{
       "orgId": 1000001,
       "name" : "testname",
       "taxExempt" : "true",
       "businessPartnerGroupId" : 1000013
   }' 
