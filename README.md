# Authentication Service Using Kotlin & Spring

## Endpoints

### GET `/`

Requires registration and login.

### POST `/api/login`

requires a registered and confirmed account and respond back with a valid JWT token if successful. The token has to be sent with each authenticated request in the header.

request:
```
{
    "email" : "test@test.com",
    "password" : "12345678"
}
```

response: 
```
{
     "email": "asd@tes.come",
     "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhc2RAdGVzLmNvbWUiLCJpYXQiOjE1NzIwMzk5NzksImV4cCI6MTU3MjA0MzU3OX0.wTFlKBfEyw1jQMQ_dO0dgmYf7gVkYHZxc5hQRBXKUmU"
}
```

### POST `/api/register`

Requires a non blank and valid email, name and password.

request:
```
{
	"name":"Nelson",
	"email":"nelson@mandela.com",
	"password":"Test@123"
}		
```

response: 
```
{
    "response": "User created successfully"
}
```

### GET `/api/register/:email/:token`

Has to be called for the account to be confirmed. Token should have been sent to the user's email but for now it can be accessed using the admin endpoint below.

response: 
```
{
    "response": "User confirmed successfully"
}
```


### POST `/api/password_reset`

Called to request a password change for a specific account. A valid reset token should have been sent to the user's email but for now it can be accessed using the admin endpoint below.

request:
```
{
	"email":"test@test.com"
}	
```

response: 
```
{
    "response": "Password change request initiated, please check your email"
}
```

### PUT `/api/password_reset`

Called with a valid and confirmed email who requested a change to its password, a new password and the reset token that was send to his email.

request:
```
{
	"email":"test@test.com",
	"password":"testtest",
	"token":"dba8158a-685d-467b-ac6b-38cb2ed3e2af"
}
```

response: 
```
{
    "response": "Password changed successfully"
}
```


### GET `/admin/password_reset_token/:email`

Used as a testing endpoint to get the password reset token

response: 
```
{
    "name": "testing",
    "email": "asd@tes.come",
    "is_password_reset_requested": false,
    "password_reset_token": "87afbf1b-96ef-436a-b8bd-f05a53e2ebee"
}
```

### GET `/admin/email_confirmation_token/:email`

Used as a testing endpoint to get the email confirmation token

response: 
```
{
    "name": "testing",
    "email": "asd@tes.come",
    "is_registration_confirmed": false,
    "registration_token": "87afbf1b-96ef-436a-b8bd-f05a53e2ebee"
}
```

## Tests

There are 23 [tests](/src/test/kotlin/com/example/authenticationservice/AuthenticationServiceApplicationTest.kt) that cover most of the functionality described above.

