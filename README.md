# Debt Montoring Admin Tool

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for testing purposes.

### Prerequisites

Java OpenJDK 14 or greater should be installed on your machine.

```
Go to: https://jdk.java.net/archive/ to download and install.
```

### Installing

Download the file DebtsAdminTool.tar.gz and unpack the DebtAdminTool.jar file.

```
gunzip -c foo.tar.gz | tar xopf -
```

## Running the tests

The Debts Admin Tool is a debt monitoring tool that has two main functions.

* List all debts and output the data as a JSON string.
* Retrieve a single debt and output data as a JSON string.

### List All Debts

Run the following command from your command line shell to list all debts and output the data as a JSON string.

```
java -jar DebtsAdminTool.jar

```

### Produces the following JSON Response

```
[ {
  "id" : 0,
  "amount" : 123.46,
  "is_in_payment_plan" : true,
  "remaining_amount" : 0.0,
  "next_payment_due_date" : null
}, {
  "id" : 1,
  "amount" : 100.0,
  "is_in_payment_plan" : true,
  "remaining_amount" : 50.0,
  "next_payment_due_date" : "2020-08-15T00:00:00Z"
}, {
  "id" : 2,
  "amount" : 4920.34,
  "is_in_payment_plan" : true,
  "remaining_amount" : 607.67,
  "next_payment_due_date" : "2020-08-26T00:00:00Z"
}, {
  "id" : 3,
  "amount" : 12938.0,
  "is_in_payment_plan" : true,
  "remaining_amount" : 622.415,
  "next_payment_due_date" : "2020-08-22T00:00:00Z"
}, {
  "id" : 4,
  "amount" : 9238.02,
  "is_in_payment_plan" : false,
  "remaining_amount" : 9238.02,
  "next_payment_due_date" : null
} ]

```

### Retrieve Single Debt

Run the following command to retrieve a single debt and output the data as a JSON string.
```
java -jar DebtsAdminTool.jar 2

```
### Produces the following JSON Response
```
{
  "id" : 2,
  "amount" : 4920.34,
  "is_in_payment_plan" : true,
  "remaining_amount" : 607.67,
  "next_payment_due_date" : "2020-08-26T00:00:00Z"
}

```

## Assumptions

* The remaining_amount calculation is rounded up to 3 decimal places.
* When calculating the next payment due date I determine if the last payment was made earlier than the scheduled due date.  For example: For a BI WEEKLY payment schedule starting 2020-01-01, a payment was made on 2020-08-08 which falls between 2020-07-29 and 2020-08-12 scheduled payment dates.  Therefore, I assume the payment was for the 2020-08-12 scheduled payment due date.  Therefore, the next payment will fall to the 2020-08-26 scheduled due date.
* If the time portion is not available in the start_date then I default to T00:00:00Z when calculating the next payment due date.

