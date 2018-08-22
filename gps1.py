import serial
import re
import time
import urllib
import urlparse
 
import spidev
import os
import math
import MySQLdb







#port = "/dev/ttyAMA0"  # Raspberry Pi 2
#port = "/dev/ttyS0"    # Raspberry Pi 3

def parseGPS(data):
#    print "raw:", data
    try:
        if data[0:6] == "$GPGGA":
            s = data.split(",")
            if s[7] == '0':
                print "no satellite data available"
                return        
            time = s[1][0:2] + ":" + s[1][2:4] + ":" + s[1][4:6]
            lat = decode(s[2])
            #print(lat)
            a=lat[0:2]
            #print(a)
            b=(float(lat[2:4]))/60
            c=(float(lat[5:7]))/(60*60)
            val=float(c)+float(b)+float(a)
            v=val-0.00880111
            print(v)
            dirLat = s[3]
            lon = decode(s[4])
            #print(lon)
            d=lon[0:3]
            #print(d)
            e=(float(lon[3:5]))/60
            f=(float(lon[5:7]))/(60*60)
            val1=float(d)+float(e)+float(f)
            v1=val1+0.00525
            print(v1)
            
            dirLon = s[5]
            alt = s[9] + " m"
            sat = s[7]
            try:
                #url="http://devicedemo01.000webhostapp.com/latlon.php?latitude="+str(val)+"&longitude="+str(val1)
                #params={'latitude':val,'longitude':val1,'heart':0,'temperature':0}
                #post_params=urllib.urlencode(params);
                #print(post_params)
                #urllib.urlopen(url);
                con.execute("UPDATE `state` SET `latitude`="+str(v)+",`longitude`="+str(v1)+" WHERE 1")
                db.commit()
                print("data sent")
            except:
                print("not connected")
            
    except:
            print("reconnect to gps")
        
        

def decode(coord):
    # DDDMM.MMMMM -> DD deg MM.MMMMM min

    
    v = coord.split(".")
    head = v[0]
    tail =  v[1]
    deg = head[0:-2]
    min = head[-2:]
    #degg=int(deg+10)
    #return (int(((deg+((min*60)+tail)/3600))))
    return deg  + min + "." + tail 
    #return value
        
ser = serial.Serial(port='/dev/ttyS0', baudrate = 9600)
while True:
    db=MySQLdb.connect("localhost","root","root","location")
    con=db.cursor()
    data = ser.readline()
    parseGPS(data)
    #templevel=ReadChannel(temp_channel)
   # temperature=ConvertTemp(templevel,2)
    #bpm = p.BPM
    
    #url="http://devicedemo01.000webhostapp.com/hearttemp.php?&heart="+str(bpm)+"&temperature="+str(temperature)
    time.sleep(1)

    

