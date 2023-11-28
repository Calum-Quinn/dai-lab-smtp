# DAI - Labo SMTP

## Description

This project is a Java application that allows the user to send emails to a list of victims. At the start of the
program, The user must provide a list of email addresses, a list of messages to be sent and the number of groups to be
created. The application will then create groups of victims and send them a message.

## Prerequisites

- Java 21
- Maven 3.9.4
- Docker 24.0.6

## Installation

1. Pull the docker image for the mock SMTP server

    ```shell
    docker pull maildev/maildev
    ```

2. Clone the repository

    ```shell
    git clone https://github.com/Calum-Quinn/dai-lab-smtp.git
    ```

3. In the `Client.java` file, change the `SERVER_ADDRESS` and `SERVER_PORT` to match your mock SMTP server.

    ```java
    final String SERVER_ADDRESS = "<your server address>";
    final int SERVER_PORT = <your server port>;
    ```

4. Build the project
    ```shell
    mvn package
    ```

## Configuration

At the start of the application, the user must provide two files, the list of email addresses and the list of messages.
You can either use the provided files in the [data](./data) folder or create your own. If you create your own files, make sure to use
**json** files and follow the format below.

### Email addresses

```json
{
  "addresses": [
    {
      "address": "<email address>"
    },
    {
      "address": "<email address>"
    }
    ...
  ]
}
```

### Messages

```json
{
  "messages": [
    {
      "subject": "<subject>",
      "body": "<body>"
    },
    {
      "subject": "<subject>",
      "body": "<body>"
    }
    ...
  ]
}
```

## Running the application

1. Start the mock SMTP server

    ```shell
    docker run -d -p 1080:1080 -p 1025:1025 maildev/maildev
    ```
   
2. Run the application

    ```shell
    java -jar target/stmp-client-1.0.jar <path to email addresses> <path to messages> <number of groups>
    ```
   for example : 
    ```shell
    java -jar target/stmp-client-1.0.jar data/addresses.json data/messages.json 3
    ```
   
That's it, the application will now send the messages to the victims.

## Implementation

### Class diagram

### Examples of dialogues with the SMTP server
  