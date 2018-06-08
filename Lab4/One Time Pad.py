#Start of A5/1 generator Keystream
#========================================

# Operation XOR
def xor(a,b):
    global bit
    if a==b:
        bit=0
    else:
        bit=1
    return

def cycle(shift, code):
    #LFSR1
    if code == 1:
        xor(shift[18], shift[17])
        xor(bit, shift[16])
        xor(bit, shift[13])
        shift.insert(0, bit)
        del shift[19]
    #LFSR2
    if code == 2:
        xor(shift[20], shift[21])
        shift.insert(0, bit)
        del shift[22]
    #LFSR3
    if code == 3:
        xor(shift[20], shift[21])
        xor(bit, shift[22])
        xor(bit, shift[7])
        shift.insert(0, bit)
        del shift[23]

if __name__ == '__main__':
    #Setting the initial values
    sessionKey = [0, 1, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1]
    LSFR1 = sessionKey[0:19]
    LSFR2 = sessionKey[19:41]
    LSFR3 = sessionKey[41:64]
    keyStream = []
    bit = 0

    #Generating keyStream. Its lenght is equal to m
    n = 0
    m = 31 # 31 max if arduino is sending a binary
    while n < m:
        #the ouptut is a XOR of the first, the second and the third LFSR
        xor(LSFR1[18], LSFR2[21])
        xor(bit, LSFR3[22])
        keyStream.insert(0, bit)

        #In each round all LFSRs are moving
        cycle(LSFR1, 1)
        cycle(LSFR2, 2)
        cycle(LSFR3, 3)
        n += 1

    temp_keystream = "".join(str(i) for i in keyStream)

# End of A5/1 generatorKeystream
#========================================



# Start of UART communication
#========================================
def sendToArduino(sendStr):
    ser.write(sendStr)

def recvFromArduino():
    global startMarker, endMarker

    ck = ""
    x = "z"  # any value that is not an end- or startMarker
    byteCount = -1  # to allow for the fact that the last increment will be one too many

    # wait for the start character
    while ord(x) != startMarker:
        x = ser.read()

    # save data until the end marker is found
    while ord(x) != endMarker:
        if ord(x) != startMarker:
            ck = ck + x
            byteCount += 1
        x = ser.read()

    return (ck)

def waitForArduino():
    # wait until the Arduino sends 'Arduino Ready' - allows time for Arduino reset
    # it also ensures that any bytes left over from a previous message are discarded

    global startMarker, endMarker

    msg = ""
    while msg.find("Arduino is ready") == -1:

        while ser.inWaiting() == 0:
            pass

        msg = recvFromArduino()

        print msg
        print
        print "======================================================="

def sendKeystream(td):
    numLoops = len(td)
    waitingForReply = False

    n = 0
    while n < numLoops:

        teststr = td[n]

        if waitingForReply == False:
            sendToArduino(teststr)
            print "Sent from PC KEYSTREAM: " + teststr
            waitingForReply = True

        if waitingForReply == True:

            while ser.inWaiting() == 0:
                pass

            dataRecvd = recvFromArduino()
            print "Reply from Arduino UNO ==> Received " + dataRecvd
            n += 1
            waitingForReply = False

            print "======================================================="

        time.sleep(5)
# ======================================

# Connection with Arduino
# ======================================

import serial
import time

print
print

# Start connection with Arduino
serPort = "COM5"
baudRate = 9600
ser = serial.Serial(serPort, baudRate)
print "Serial port " + serPort + " opened  Baudrate " + str(baudRate)

startMarker = 60
endMarker = 62

waitForArduino()

testData = []
testData.append("<" + temp_keystream + ">")

sendKeystream(testData)

ser.close