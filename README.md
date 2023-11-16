# DAI Labo SMTP

## Description

## Setup

## Configuration

## Implementation
The user must provide three command line arguments.
- The path to the file containing the email addresses
- The path to the file containing the messages
- The nummber of groups to be created

The number of people (email addresses) in each group will in part depend on the number of groups desired as well as the number of possible email addresses.

These are the commands that are sent from the client to the server:
- `ehlo heig-vd.ch`
- `mail from:<senderEmail>`
- `rcpt to:<recipientEmail>`
- `rcpt to:<recipientEmail>`
- `...`
- `data`
- ```From: <senderEmail>
     To: 
	 Date: November 30th, 2023
	 Subject: Hello
	 
	 Hi, this is spoof
	 
	 .
	 ```
- `quit`
  