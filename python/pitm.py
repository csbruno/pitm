import socket
import socketserver
import pyvjoy
import json
import time

"""

Constants
"""


#DDAP
DPAD_UP = 19
DPAD_DOWN = 20
DPAD_LEFT = 21
DPAD_RIGHT = 22
#a
BUTTON_X = 98
BUTTON_Y = 99
BUTTON_A = 96
BUTTON_B = 97
#Menu
BUTTON_VIEW = 102
BUTTON_MENU = 103
XBOX = 82
#Triggers
TRIGGER_LEFT = 100
TRIGGER_RIGHT = 101
#Sticks
STICK_LEFT = 104
STICK_RIGHT =105
#Anl
RIGHT_STICK_X =990
RIGHT_STICK_Y =991                 
LEFT_STICK_X = 992
LEFT_STICK_Y = 993
#triggers
LEFT_TRIGGER_AXIS = 998
RIGHT_TRIGGER_AXIS = 999


#PU
port = 8888
host = socket.gethostbyname(socket.gethostname())

device = pyvjoy.VJoyDevice(1)


def getButtonValue(x):
    return {
        DPAD_UP:    1,
        DPAD_DOWN:  2,
        DPAD_LEFT:  3,
        DPAD_RIGHT: 4,
        BUTTON_X:   5,
        BUTTON_Y:   6,
        BUTTON_A:   7,
        BUTTON_B:   8,
        BUTTON_VIEW:9,
        BUTTON_MENU:10,
        XBOX      :11,
        TRIGGER_LEFT:12,
        TRIGGER_RIGHT: 13,
        STICK_LEFT:    14,
        STICK_RIGHT:   15,
        
    }[x]
def getlButtons(buttons):
    result = 0x0
    for b in buttons: result ^=  (0x1 << (b-1))
    return result


def setItems(data):
    
    listButtons=[]
    for key, value in data.items():
        if(key =="TIME"):
            continue
        if(int(key) < 200):
            if(int(value) ==1):
                try:
                    listButtons.append(getButtonValue(int(key)))
                except:
                    print("Unknown key code: {0} ".format(key))
        else:
            if(int(key) == RIGHT_STICK_X):
                device.data.wAxisXRot = int(value)
            elif(int(key)== RIGHT_STICK_Y):
                device.data.wAxisYRot = int(value)
            elif(int(key)== LEFT_STICK_X):
                device.data.wAxisX = int(value)
            elif(int(key) == LEFT_STICK_Y):
                device.data.wAxisY = int(value)
            elif(int(key) == RIGHT_TRIGGER_AXIS):
                device.data.wSlider = int(value)
            elif(int(key) == LEFT_TRIGGER_AXIS):
                device.data.wDial = int(value)
    device.data.lButtons = getlButton(listButtons)
    device.update()
     
            
class UDPHandler(socketserver.BaseRequestHandler):
    def handle(self):
        data = self.request[0].strip()
        sc = self.request[1]
        sc.setsockopt(socket.SOL_SOCKET, socket.SO_RCVBUF, 1)
        self.readData(data)
        sc.sendto(data.upper(), self.client_address)
    def readData(self,data):
        d =  (data.decode())
        setItems(json.loads(d))
                
    
if __name__ == "__main__":
    while True:
        useri = input("Is your ip correct {0} ? (Y/ N)\n".format(host))
        if(useri.lower() == "y"):
            break
        elif(useri.lower() == "n"):
            host = input("Type your local IP")
        else:
            print("Wrong input")
            
    server = socketserver.UDPServer((host, port), UDPHandler)
    print('Server open in {0}:{1}'.format(host, port))
    server.serve_forever()
 
