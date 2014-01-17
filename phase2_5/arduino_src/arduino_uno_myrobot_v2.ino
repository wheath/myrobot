#include <Servo.h> 

const int ledPin = 11;   // Teensy has LED on 11, Teensy++ on 6

int led_on = 1;
int pos = 0;
int speed = 180;
Servo servo_right, servo_left;  // create servo object to control a servo 
                // a maximum of eight servo objects can be created 
char incomingByte; // Holds incoming serial values
// This line defines a "Uart" object to access the serial port

void setup()   {                
  Serial.begin(9600);

  pinMode(ledPin, OUTPUT);
 
}


void attach_servos() {
  servo_left.attach(9);
  servo_right.attach(10);  // attaches the servo on pin B5
}

void detach_servos() {
  servo_left.detach();
  servo_right.detach();
}

void toggle_led() {
  if(led_on) {
    led_on = 0;
    digitalWrite(ledPin, HIGH);   // set the LED on
  } else {
    led_on = 1;
    digitalWrite(ledPin, LOW);
  }
}

void handle_input(char i) {
  
  switch(i) {
        case 'r':
        case 'R':
          toggle_led();
          attach_servos();
          servo_left.write(speed);
          servo_right.write(speed);
          delay(1000); 
          detach_servos();
          toggle_led();
          break;
        case 'l':
        case 'L':
          toggle_led();
          attach_servos();
          servo_left.write(-speed);
          servo_right.write(-speed);
          delay(1000); 
          detach_servos();
          toggle_led();
          break;
        case 'b':
        case 'B':
          toggle_led();
          attach_servos();
          servo_left.write(-speed);
          servo_right.write(speed);
          delay(500); 
          detach_servos();
          toggle_led();
          break;
        case 'f':
        case 'F':
          toggle_led();
          attach_servos();
          servo_left.write(speed);
          servo_right.write(-speed);
          delay(500); 
          detach_servos();
          toggle_led();
          break;
        case 's':
        case 'S':
          toggle_led();
          detach_servos();
          toggle_led();
          break;
      }
}

void loop() {
  if (Serial.available() > 0) {
    incomingByte = Serial.read();
    handle_input(incomingByte);
  }
}

