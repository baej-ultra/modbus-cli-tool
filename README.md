**[WORK IN PROGRESS]**

## What is this?

It's a simple CLI tool for polling Modbus devices/slaves and starting a virtual server(slave) 

## Client:
- Supported Modbus function: 0x01, 0x02, 0x03, 0x04
- Various display formats

## Server
- Supported Modbus functions: 0x05, 0x06, 0x15, 0x16

### Address table 
#### **Holding/Input registers:**
- 0-4 short int (16b) 
- 5-14 int (32b) 
- 15-34 long (64b)
- 35-44 float (32b)
- 45-64 double (64b)
  
#### **Discrete Inputs/Coils**
- 0-7 all true
- 8-15 all false
- 16-23 random

# How to run
Linux/Mac
<br>`mvnw spring-boot:run`
<br>
<br>Windows
<br>`mvnw.cmd spring-boot:run`
<br><br>Or Just build with Maven
<br>`mvn clean package -DskipTests`

![Alt Text](https://s3.gifyu.com/images/bSqxq.gif)
