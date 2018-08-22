#!/usr/bin/python
 
import spidev
import time
import os
import MySQLdb
count=0
count1=0
# Open SPI bus
spi = spidev.SpiDev()
spi.open(0,0)
spi.max_speed_hz=1000000
 
# Function to read SPI data from MCP3008 chip
# Channel must be an integer 0-7
def ReadChannel(channel):
  adc = spi.xfer2([1,(8+channel)<<4,0])
  data = ((adc[1]&3) << 8) + adc[2]
  return data
 
# Function to convert data to voltage level,
# rounded to specified number of decimal places.
def ConvertVolts(data,places):
  volts = (data * 3.3) / float(1023)
  volts = round(volts,places)
  return volts
 
# Function to calculate temperature from
# TMP36 data, rounded to specified
# number of decimal places.
def ConvertTemp(data,places):
 
  # ADC Value
  # (approx)  Temp  Volts
  #    0      -50    0.00
  #   78      -25    0.25
  #  155        0    0.50
  #  233       25    0.75
  #  310       50    1.00
  #  465      100    1.50
  #  775      200    2.50
  # 1023      280    3.30
 
  temp = ((data * 3.3)/1023)
  temp = round(temp,places)
  return temp
 
# Define sensor channels
light_channel = 0
temp_channel  = 2
 
# Define delay between readings
delay = 5
 
while True:
  db=MySQLdb.connect("localhost","root","root","location")
  con=db.cursor()
 
  # Read the light sensor data
  light_level = ReadChannel(light_channel)
  light_volts = ConvertVolts(light_level,2)
 
  # Read the temperature sensor data
  temp_level = ReadChannel(temp_channel)
  temp_volts = ConvertVolts(temp_level,2)
  temp       = ConvertTemp(temp_level,2)
 
  # Print out results
  
  print("alochol : {} ".format(temp_level))
  con.execute("UPDATE `state` SET `alcohol`="+str(temp_level)+" WHERE 1")
  db.commit()
  if(temp_level<=190):
      count1=count1+1
      if(count1==2):
          con.execute("UPDATE `state` SET `button`=2 WHERE 1")
          db.commit()
  else:
      count=count+1
      if(count==5):
          con.execute("UPDATE `state` SET `button`=0 WHERE 1")
          db.commit()
      time.sleep(0.3)
      
  # Wait before repeating loop
  time.sleep(0.2)