#include <IRremote.h>

IRsend irsend;
int RECV_PIN = 11;
IRrecv irrecv(RECV_PIN);
decode_results results;

const byte numLEDs = 2;
byte ledPin[numLEDs] = {12, 13};
unsigned long LEDinterval[numLEDs] = {200, 400};
unsigned long prevLEDmillis[numLEDs] = {0, 0};

const byte buffSize = 33;
char inputBuffer[buffSize];
const char startMarker = '<';
const char endMarker = '>';
byte bytesRecvd = 0;
boolean readInProgress = false;
boolean newDataFromPC = false;
unsigned long curMillis;

char messageFromPC[buffSize] = {0};
int newFlashInterval = 0;

//long plaintext = 0b01101000011011110110110001100001; //hola in binary
char plaintext[buffSize] = "01101000011011110110110001100001"; //hola in binary
String value = "";
unsigned long result = 0b0;
//long xored = 0;

void setup() {
  Serial.begin(9600);
  
    // flash LEDs so we know we are alive
  for (byte n = 0; n < numLEDs; n++) {
     pinMode(ledPin[n], OUTPUT);
     digitalWrite(ledPin[n], HIGH);
  }
  delay(500); // delay() is OK in setup as it only happens once
  
  for (byte n = 0; n < numLEDs; n++) {
     digitalWrite(ledPin[n], LOW);
  }
  
    // tell the PC we are ready
  Serial.println("<Arduino is ready. Send something interesting>");

  //uncomment next lines in order to be the receiver
  //Serial.println("Enabling IRin");
  //irrecv.enableIRIn(); // Start the receiver
  //Serial.println("Enabled IRin");

}

void loop() {
  getDataFromPC();
  updateFlashInterval();
  flashLEDs();
  replyToPC();
  //transmitterIrDA();
  //receiverIrDA();  //uncomment this line and comment previous line in order to be the receiver
  
}

void getDataFromPC() {

    // receive data from PC and save it into inputBuffer
  if(Serial.available() > 0) {

    char x = Serial.read();

    if (x == endMarker) {
      readInProgress = false;
      newDataFromPC = true;
      inputBuffer[bytesRecvd] = 0;
      parseData();
    }
    
    if(readInProgress) {
      inputBuffer[bytesRecvd] = x;
      bytesRecvd ++;
      if (bytesRecvd == buffSize) {
        bytesRecvd = buffSize - 1;
      }
    }

    if (x == startMarker) { 
      bytesRecvd = 0; 
      readInProgress = true;
    }
  }
}

void parseData() {

  strcpy(messageFromPC, inputBuffer); // copy it to messageFromPC
}

void replyToPC() {

 if (newDataFromPC) {
    newDataFromPC = false;

    Serial.print("<CIPHERTEXT: ");
    int i;
    for(i=0; i<buffSize-1; ++i){
        Serial.print(messageFromPC[i] ^ plaintext[i]);        
    }
    Serial.println(">");
  }
}

void updateFlashInterval() {

   // this illustrates using different inputs to call different functions
  if (strcmp(messageFromPC, "LED1") == 0) {
     updateLED1();
  }
  
  if (strcmp(messageFromPC, "LED2") == 0) {
     updateLED2();
  }
}

void updateLED1() {

  if (newFlashInterval > 100) {
    LEDinterval[0] = newFlashInterval;
  }
}

void updateLED2() {

  if (newFlashInterval > 100) {
    LEDinterval[1] = newFlashInterval;
  }
}

void flashLEDs() {

  for (byte n = 0; n < numLEDs; n++) {
    if (curMillis - prevLEDmillis[n] >= LEDinterval[n]) {
       prevLEDmillis[n] += LEDinterval[n];
       digitalWrite( ledPin[n], ! digitalRead( ledPin[n]) );
    }
  }
}


//IdRA communication
void transmitterIrDA() {

    for (int i = 0; i <= buffSize; i++) {
        irsend.sendSony(messageFromPC[i] ^ plaintext[i], 12);
        delay(40);
    }
    delay(5000); //5 second delay between each signal burst
}

void receiverIrDA() {
    if (irrecv.decode(&results)) {
    Serial.println(results.value, BIN);
    irrecv.resume(); // Receive the next value
  }
  delay(100);
}