from pulsesensor import Pulsesensor
import time
import urlparse
import urllib
import MySQLdb

p = Pulsesensor()
p.startAsyncBPM()

try:
    while True:
        db=MySQLdb.connect("localhost","root","root","location")
        con=db.cursor()
        bpm = p.BPM
        if bpm > 0:
            print("BPM: %d" % bpm)
            #url="http://presentationism-fan.000webhostapp.com/update.php?heart="+str(bpm)
            #urllib.urlopen(url);   
        else:
            print("No Heartbeat found")
        time.sleep(1)
        con.execute("UPDATE `state` SET `heart`="+str(bpm)+" WHERE 1")
        db.commit()
        if(bpm>=90):
            con.execute("UPDATE `state` SET `button`=1 WHERE 1")
            db.commit()
        else:
            con.execute("UPDATE `state` SET `button`=0 WHERE 1")
            db.commit()
            
            
except:
    p.stopAsyncBPM()
