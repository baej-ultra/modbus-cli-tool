**[WORK IN PROGRESS]**

## What is this?

It's a simple CLI tool for polling Modbus devices/slaves and starting a virtual server(slave) 

## How's it going:
### Client:
- Supported Modbus function: 0x01, 0x02, 0x03, 0x04
- Various display formats
### Server
Rough implementation with random values generated with each poll
<p>
Address table (holding registers):
<br>0-4 short int (16b) 
<br>5-14 int (32b) 
<br>15-34 long (64b)
<br>35-44 float (32b)
<br>45-64 double (64b)


![Alt Text](https://s3.gifyu.com/images/bSqxq.gif)

## Thing's to implement
- Write functions for client
- 95% of server functions