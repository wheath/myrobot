//==============================================================================
//                             _
//                            | | (_)
//   _ __ ___  _ __ ___   ___ | |_ ___   _____
//  | '__/ _ \| '_ ` _ \ / _ \| __| \ \ / / _ \
//  | | | (_) | | | | | | (_) | |_| |\ V /  __/
//  |_|  \___/|_| |_| |_|\___/ \__|_| \_/ \___|
//
//==============================================================================
//
//  ViewController.m
//  HelloRMCore
//
//  Created by Romotive on 5/21/13.
//  Copyright (c) 2013 Romotive. All rights reserved.
//
//==============================================================================
#import "ViewController.h"
#import "Toast.h"

@interface ViewController () {
    
     NSTimer *_timer;
     NSTimer *_move_timer;
}
- (void)_timerFired;

- (void)layoutForConnected;
- (void)layoutForUnconnected;

@end

@implementation ViewController{

    BOOL isLoggedIn;
    BOOL isAvailable;
    OTSession* _session;
    OTPublisher* _publisher;
    OTSubscriber* _subscriber;
    NSString *cellbot_nid;
    /*
    NSString *api_key;
    NSString *session_id;
    NSString *token_id;
     */
    
    
}

static double widgetHeight = 240;
static double widgetWidth = 320;

// *** Fill the following variables using your own Project info from the Dashboard  ***
// ***                   https://dashboard.tokbox.com/projects***

/*
static NSString* const kApiKey = @"22089962";    // Replace with your OpenTok API key
static NSString* const kSessionId = @"2_MX4yMjA4OTk2Mn5-V2VkIE5vdiAyMCAwMToxNTo1OCBQU1QgMjAxM34wLjI5NDY0NTczfg"; // Replace with your generated session ID
static NSString* const kToken = @"T1==cGFydG5lcl9pZD0yMjA4OTk2MiZzZGtfdmVyc2lvbj10YnJ1YnktdGJyYi12MC45MS4yMDExLTAyLTE3JnNpZz05YWQwYTA2ZTcwOWZiYjc2ODAyNjQyOTFiYjk0MDJiYjcwZGEyMTEwOnJvbGU9cHVibGlzaGVyJnNlc3Npb25faWQ9Ml9NWDR5TWpBNE9UazJNbjUtVjJWa0lFNXZkaUF5TUNBd01Ub3hOVG8xT0NCUVUxUWdNakF4TTM0d0xqSTVORFkwTlRjemZnJmNyZWF0ZV90aW1lPTEzODQ5Mzk4MDAmbm9uY2U9MC42NDc2OTkwMTEzMDUxMjU0JmV4cGlyZV90aW1lPTEzODc1MzE3OTkmY29ubmVjdGlvbl9kYXRhPQ==";     // Replace with your generated token (use the Dashboard or an OpenTok server-side library)
 */





static NSString* kApiKey;    // Replace with your OpenTok API key
static NSString* kSessionId; // Replace with your generated session ID
static NSString* kToken;     // Replace with your generated token (use the Dashboard or an OpenTok server-side library)
 


static bool subscribeToSelf = NO; // Change to NO to subscribe to streams other than your own.
#pragma mark -- View Lifecycle --

- (void)viewDidLoad
{
    [super viewDidLoad];
  
    
    /*
    _session = [[OTSession alloc] initWithSessionId:kSessionId
                                           delegate:self];
     */
    [self doConnect];
     

    
    // Assume the Robot is not connected
    // [self layoutForUnconnected];
    [self layoutForConnected];

    // To receive messages when Robots connect & disconnect, set RMCore's delegate to self
    [RMCore setDelegate:self];
    isLoggedIn = NO;
    isAvailable = NO;
    self.goAvailableButton.enabled = NO;
    [UIApplication sharedApplication].idleTimerDisabled = YES;
}





#pragma mark -- RMCoreDelegate Methods --

- (void)robotDidConnect:(RMCoreRobot *)robot
{
    // Currently the only kind of robot is Romo3, which supports all of these
    //  protocols, so this is just future-proofing
    if (robot.isDrivable && robot.isHeadTiltable && robot.isLEDEquipped) {
        
        self.robot = (RMCoreRobot<HeadTiltProtocol, DriveProtocol, LEDProtocol> *) robot;
        
        // Change the robot's LED to be solid at 80% power
        [self.robot.LEDs setSolidWithBrightness:0.8];
        
        [self layoutForConnected];
    }
}

- (void)robotDidDisconnect:(RMCoreRobot *)robot
{
    if (robot == self.robot) {
        self.robot = nil;
        
        [self layoutForConnected];
        
       // [self layoutForUnconnected];
    }
}

#pragma mark -- IBAction Methods --

- (void)didTouchDriveInCircleButton:(UIButton *)sender
{
    // If the robot is driving, let's stop driving
    if (self.robot.isDriving) {
        // Change the robot's LED to be solid at 80% power
        [self.robot.LEDs setSolidWithBrightness:0.8];
        
        // Tell the robot to stop
        [self.robot stopDriving];
        
        [sender setTitle:@"Drive in circle" forState:UIControlStateNormal];
    } else {
        // Change the robot's LED to pulse
        [self.robot.LEDs pulseWithPeriod:1.0 direction:RMCoreLEDPulseDirectionUpAndDown];
        
        // Romo's top speed is around 0.75 m/s
        float speedInMetersPerSecond = 0.5;
        
        // Drive a circle about 0.25 meter in radius
        float radiusInMeters = 0.25;
        
        // Give the robot the drive command
        [self.robot driveWithRadius:radiusInMeters speed:speedInMetersPerSecond];
        
        [sender setTitle:@"Stop Driving" forState:UIControlStateNormal];
    }
}

- (void)didTouchTiltUpButton:(UIButton *)sender
{
    // If the robot is tilting, stop tilting
    if (self.robot.isTilting) {
        
        // Tell the robot to stop tilting
        [self.robot stopTilting];
        
        [sender setTitle:@"Tilt Up" forState:UIControlStateNormal];
        
    } else {
        
        [sender setTitle:@"Stop" forState:UIControlStateNormal];
        
        // Tilt down by ten degrees
        float tiltByAngleInDegrees = 10.0;
        
        [self.robot tiltByAngle:tiltByAngleInDegrees
                     completion:^(BOOL success) {
                         // Reset button title on the main queue
                         dispatch_async(dispatch_get_main_queue(), ^{
                             [sender setTitle:@"Tilt Up" forState:UIControlStateNormal];
                         });
                     }];
    }
}

- (void)didTouchTiltDownButton:(UIButton *)sender
{
    // If the robot is tilting, stop tilting
    if (self.robot.isTilting) {
        
        // Tell the robot to stop tilting
        [self.robot stopTilting];
        
        [sender setTitle:@"Tilt Down" forState:UIControlStateNormal];
        
    } else {
        
        [sender setTitle:@"Stop" forState:UIControlStateNormal];
        
        // Tilt up by ten degrees
        float tiltByAngleInDegrees = -10.0;
        
        [self.robot tiltByAngle:tiltByAngleInDegrees
                     completion:^(BOOL success) {
                         // Reset button title on the main queue
                         dispatch_async(dispatch_get_main_queue(), ^{
                             [sender setTitle:@"Tilt Down" forState:UIControlStateNormal];
                         });
                     }];
    }
}

- (void)didTouchLoginDrupalButton:(UIButton *)sender
{
    NSLog(@"you clicked on Login Drupal button");
    
    
    if(isLoggedIn) {
      [self logoffDrupal];
      self.label.text = @"Not logged in";
      isLoggedIn = NO;
        [self.goAvailableButton setTitle:@"Go Available" forState:UIControlStateNormal];
        self.goAvailableButton.enabled = NO;
        [sender setTitle:@"Login" forState:UIControlStateNormal];
        isAvailable = NO;
        
    } else {
      [self loginDrupal];
      
        if(isLoggedIn) {
      self.label.text = @"Logged in";
      
               
      
        
        NSMutableString *string1 = [NSMutableString stringWithString:@"Succesful login for cellbot nid: "];
        
        
        
        [string1 appendString:cellbot_nid];
        Toast *mToast = [Toast toastWithMessage:string1];
        
        
        [mToast showOnView:self.view];
        self.goAvailableButton.enabled = YES;
        [sender setTitle:@"Logout" forState:UIControlStateNormal];
        }
    }
    
        
}

-(void)loginDrupal {
    
    
    
    NSMutableString *requestString = [NSMutableString stringWithString:@"username="];
    
    [requestString appendString:self.userNameTextField.text];
    [requestString appendString:@"&password="];
    [requestString appendString:self.passwordTextField.text];
     
     
     

    
   /*
        NSString *requestString = [NSString stringWithFormat:@"username=MyRobotadmin&password=myrobot",nil];
    */
     
     
     
    NSLog(requestString);
    
    
    NSData *requestData = [NSData dataWithBytes: [requestString UTF8String] length: [requestString length]];
    
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: @"http://www.myrobot.com/drupal/rest/user/login.json"]];
    
    NSString *postLength = [NSString stringWithFormat:@"%d", [requestData length]];
    [request setHTTPMethod: @"POST"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody: requestData];
    
    //Data returned by WebService
    NSData *returnData = [NSURLConnection sendSynchronousRequest: request returningResponse: nil error: nil ];
    NSString *returnString = [[NSString alloc] initWithData:returnData encoding: NSUTF8StringEncoding];
    
    NSLog(returnString);
        
    if ([returnString rangeOfString:@"Already logged in as "].location!= NSNotFound) {
            NSLog(@"string does contain Already logged in as");
                NSLog(@"_dbg already logged in");
        Toast *mToast2 = [Toast toastWithMessage:@"Already logged in, logging you out, please login again"];
        
        
        [mToast2 showOnView:self.view];

        [self logoffDrupal];
        return;
    }
    
    if([returnString isEqualToString:@"[\"Wrong username or password.\"]"]) {
        
        NSLog(@"_dbg authentication failed");
       
        
        //[self showAlert:string4];
       
        [self showAlert:@"Wrong username or password"];
        isLoggedIn = NO;
        return;
    
    
    }
    NSDictionary *result = [NSJSONSerialization JSONObjectWithData:[returnString dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    
    
    self.sessid = [result objectForKey:@"sessid"];
    self.sess_name = [result objectForKey:@"sess_name"];
    NSLog(@"%@", self.sessid);
    
    NSDictionary *user = [result objectForKey:@"user"];
    NSDictionary *field_cellbot = [user objectForKey:@"field_cellbot"];
    
    NSArray *ar1 = [field_cellbot objectForKey:@"und"];
    
    NSDictionary *nid_obj = [ar1 objectAtIndex:0];
    NSLog(@"_dbg nid: ");
    NSLog(@"%@", [nid_obj objectForKey:@"nid"]);
    cellbot_nid = [nid_obj objectForKey:@"nid"];
    isLoggedIn = YES;
    [self getCellbotInfo];
    
}



- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    NSLog(@"username entered %@",self.userNameTextField.text);
    [textField resignFirstResponder];
    return YES;
}

- (void)didTouchGoAvailableButton:(UIButton *)sender
{
    NSLog(@"you clicked on go available button");
    
        
    if(isAvailable) {
               [sender setTitle:@"Go Available" forState:UIControlStateNormal];
        isAvailable = NO;
        [self stopTimer:nil];
        
    } else {
        
        [sender setTitle:@"Go Unavailable" forState:UIControlStateNormal];
        isAvailable = YES;
        [self startTimer:nil];
        
        
    }
}


-(void)getCellbotInfo {
    
    NSLog(@"_dbg in getCellbotInfo");
    NSString *requestString = [NSString stringWithFormat:@"",nil];
    NSLog(requestString);
    
    
    NSData *requestData = [NSData dataWithBytes: [requestString UTF8String] length: [requestString length]];
    NSMutableString *URL = [NSMutableString stringWithString:@"http://www.myrobot.com/drupal/rest/node/"];
    
    
    
    [URL appendString:cellbot_nid];
    [URL appendString:@".json"];
    
    
    NSLog(URL);
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: URL]];
    
    NSString *postLength = [NSString stringWithFormat:@"%d", [requestData length]];
    [request setHTTPMethod: @"GET"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody: requestData];
    
    //Data returned by WebService
    NSData *returnData = [NSURLConnection sendSynchronousRequest: request returningResponse: nil error: nil ];
    NSString *returnString = [[NSString alloc] initWithData:returnData encoding: NSUTF8StringEncoding];
    
    NSLog(returnString);
    
     NSDictionary *result = [NSJSONSerialization JSONObjectWithData:[returnString dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    
    
    NSString *title = [result objectForKey:@"title"];
    NSLog(@"_dbg title:");
    NSLog(title);
    
    //NSDictionary *body = [result objectForKey:@"body"];
    
   
   // NSLog(body);
    NSDictionary *field_opentok_api_key = [result objectForKey:@"field_opentok_api_key"];
    NSArray *ar1 = [field_opentok_api_key objectForKey:@"und"];
    NSDictionary *api_key_obj = [ar1 objectAtIndex:0];
    kApiKey = [api_key_obj objectForKey:@"value"];
    
    NSLog(@"_dbg api_key:");
    NSLog(kApiKey);
    
    NSDictionary *field_opentok_session_id = [result objectForKey:@"field_opentok_session_id"];
    NSArray *ar2 = [field_opentok_session_id objectForKey:@"und"];
    NSDictionary *session_id_obj = [ar2 objectAtIndex:0];
    kSessionId = [session_id_obj objectForKey:@"value"];
    
    NSLog(@"_dbg session_id:");
    NSLog(kSessionId);
    
    NSDictionary *field_opentok_token_id = [result objectForKey:@"field_opentok_token_id"];
    NSArray *ar3 = [field_opentok_token_id objectForKey:@"und"];
    NSDictionary *token_id_obj = [ar3 objectAtIndex:0];
    kToken = [token_id_obj objectForKey:@"value"];
    
    NSLog(@"_dbg token_id:");
    NSLog(kToken);
    
    
    
     _session = [[OTSession alloc] initWithSessionId:kSessionId
     delegate:self];
     
      [self doConnect];
     
     
     
    
  
    
    
}



- (void)getNewCmds {
    NSString *requestString = [NSString stringWithFormat:@"",nil];
    NSLog(requestString);
    
    
    NSData *requestData = [NSData dataWithBytes: [requestString UTF8String] length: [requestString length]];
    
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: @"http://www.myrobot.com/drupal/rest/view/newcbcmds.json"]];
    
    NSString *postLength = [NSString stringWithFormat:@"%d", [requestData length]];
    [request setHTTPMethod: @"GET"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody: requestData];
    
    //Data returned by WebService
    NSData *returnData = [NSURLConnection sendSynchronousRequest: request returningResponse: nil error: nil ];
    NSString *returnString = [[NSString alloc] initWithData:returnData encoding: NSUTF8StringEncoding];
    
    NSLog(returnString);
    
    if([returnString isEqualToString:@"[]"]) {
        
        NSLog(@"_dbg no new commands found");
        
        Toast *mToast = [Toast toastWithMessage:@"No commands found in last poll"];
        
        
        [mToast showOnView:self.view];
        return;
    }

    
    
    NSArray *cmds = [NSJSONSerialization JSONObjectWithData:[returnString dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    
    int i;
    int found = 0;
    for (i = 0; i < [cmds count]; i++) {
      NSDictionary *cmd = [cmds objectAtIndex:i];
    
      NSDictionary *field_cb_node = [cmd objectForKey:@"field_cb_node_id"];
    
      NSArray *ar1 = [field_cb_node objectForKey:@"und"];
    
      NSDictionary *nid_obj = [ar1 objectAtIndex:0];
      NSString *cb_cmd_nid = [nid_obj objectForKey:@"value"];
        NSLog(@"_dbg comparing...");
      NSLog(@"_dbg cmd_cellbot_nid: ");
      NSLog(@"%@", cb_cmd_nid);
      NSLog(@"_dbg cellbot_nid: ");
      NSLog(@"%@", cellbot_nid);
        
      if([cellbot_nid isEqualToString:cb_cmd_nid]) {
        found = 1;
        break; 
      }
    
    
    }
    
    if(!found) {
        NSLog(@"_dbg no new commands found");
        
        Toast *mToast = [Toast toastWithMessage:@"No commands found in last poll"];
        
        
        [mToast showOnView:self.view];
        return;
        
    }
    
    NSDictionary *cmd = [cmds objectAtIndex:i];
    NSString *cmd_nid = [cmd objectForKey:@"nid"];
    NSLog(@"_dbg ***** cmd_nid: ");
    NSLog(@"%@", [cmd objectForKey:@"nid"]);
    NSLog(@"%@", cmd_nid);
    
    // change to processed
    
    // "http://www.myrobot.com/drupal/rest/node/" + cb_cmd_nid + ".json";
    
    NSMutableString *URL = [NSMutableString stringWithString:@"http://www.myrobot.com/drupal/rest/node/"];
    
    
    
    [URL appendString:cmd_nid];
    [URL appendString:@".json"];
    
    
    NSLog(URL);
    
    NSString *requestString2 = [NSString stringWithFormat:@"{\"field_state\":{\"und\":[{\"value\":\"processed\"}]}}",nil];
    NSLog(requestString2);
    
    
    NSData *requestData2 = [NSData dataWithBytes: [requestString2 UTF8String] length: [requestString2 length]];
    
    
    NSMutableURLRequest *request2 = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: URL]];
    
    NSString *postLength2 = [NSString stringWithFormat:@"%d", [requestData2 length]];
    [request2 setHTTPMethod: @"PUT"];
    [request2 setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request2 setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [request2 setHTTPBody: requestData2];
    
    //Data returned by WebService
    NSData *returnData2 = [NSURLConnection sendSynchronousRequest: request2 returningResponse: nil error: nil ];
    NSString *returnString2 = [[NSString alloc] initWithData:returnData2 encoding: NSUTF8StringEncoding];
    
    NSLog(returnString2);
    
    
    
    // end of change to processed
    
    // [self changeCmdToProcessed:cmd_nid];
    
    /*
    NSDictionary *field_cb_node = [cmd objectForKey:@"field_cb_node_id"];
    
    NSArray *ar1 = [field_cb_node objectForKey:@"und"];
    
    NSDictionary *nid_obj = [ar1 objectAtIndex:0];
    NSString *cb_cmd_nid = [nid_obj objectForKey:@"value"];
    NSLog(@"_dbg cmd_cellbot_nid: ");
    NSLog(@"%@", cb_cmd_nid);
    
    if(cellbot_nid == cb_cmd_nid)
     */
    
    
    
    NSDictionary *body = [cmd objectForKey:@"body"];
    
    NSArray *ar2 = [body objectForKey:@"und"];
    NSDictionary *raw_cb_cmd = [ar2 objectAtIndex:0];
    
    NSString *cb_cmd = [raw_cb_cmd  objectForKey:@"value"];
    
    
    
        
    NSMutableString *string1 = [NSMutableString stringWithString:@"command retrieved: "];
    
    
    
    [string1 appendString:cb_cmd];
    Toast *mToast = [Toast toastWithMessage:string1];
    
    
    [mToast showOnView:self.view];
    
    
        
        if([cb_cmd isEqualToString:@"r"]) {
        
        // Tilt up by ten degrees
    /*
        float tiltByAngleInDegrees = -10.0;
        
        [self.robot tiltByAngle:tiltByAngleInDegrees
                     completion:^(BOOL success) {
                         // Reset button title on the main queue
                         dispatch_async(dispatch_get_main_queue(), ^{
                             NSLog(@"something tilt");
                         });
                     }];
     
    Toast *mToast2 = [Toast toastWithMessage:@"Setting Pulse Period"];
    
    
    [mToast2 showOnView:self.view];
     */
    
    [self.robot.LEDs pulseWithPeriod:1.0 direction:RMCoreLEDPulseDirectionUpAndDown];
            
            [self.robot.LEDs pulseWithPeriod:1.0 direction:RMCoreLEDPulseDirectionUpAndDown];
            
            // Romo's top speed is around 0.75 m/s
            float speedInMetersPerSecond = 0.5;
            
            // Drive a circle about 0.25 meter in radius
            float radiusInMeters = -0.25;
            
            // Give the robot the drive command
            [self.robot driveWithRadius:radiusInMeters speed:speedInMetersPerSecond];
            
            [self startMoveTimer:nil];
    }
    
    if([cb_cmd isEqualToString:@"l"]) {
        
        [self.robot.LEDs pulseWithPeriod:1.0 direction:RMCoreLEDPulseDirectionUpAndDown];
        
        // Romo's top speed is around 0.75 m/s
        float speedInMetersPerSecond = 0.5;
        
        // Drive a circle about 0.25 meter in radius
        float radiusInMeters = 0.25;
        
        // Give the robot the drive command
        [self.robot driveWithRadius:radiusInMeters speed:speedInMetersPerSecond];
        
        [self startMoveTimer:nil];
    }
    
    if([cb_cmd isEqualToString:@"f"]) {
        
        [self.robot.LEDs pulseWithPeriod:1.0 direction:RMCoreLEDPulseDirectionUpAndDown];
        
        // Romo's top speed is around 0.75 m/s
        float speedInMetersPerSecond = 0.5;
        
        // Drive a circle about 0.25 meter in radius
                
        // Give the robot the drive command
        
        [self.robot driveWithRadius:RM_DRIVE_RADIUS_STRAIGHT
                              speed:speedInMetersPerSecond];
        
        [self startMoveTimer:nil];
    }

    if([cb_cmd isEqualToString:@"b"]) {
        
        [self.robot.LEDs pulseWithPeriod:1.0 direction:RMCoreLEDPulseDirectionUpAndDown];
        
        // Romo's top speed is around 0.75 m/s
        float speedInMetersPerSecond = -0.5;
        
       
        
        // Give the robot the drive command
        [self.robot driveWithRadius:RM_DRIVE_RADIUS_STRAIGHT
                              speed:speedInMetersPerSecond];

        
        [self startMoveTimer:nil];
    }
    
    
    
    // [self showAlert:alertMessage2];
    // [self showAlert:@"cb_cmd retrived"];
    
    /*
     [self showAlert:[NSString stringWithFormat:@"cb_cmd retrived"]];
     */
    
    NSLog(@"_dbg cb_cmd: ");
    NSLog(@"%@", cb_cmd);
    
    /*
     self.sessid = [result objectForKey:@"sessid"];
     NSLog(@"%@", self.sessid);
     */
    
    // [self changeCmdToProcessed:cmd_nid];
}

    


- (void)changeCmdToProcessed:(NSString *)cmd_nid
{
    NSLog(@"_dbg in changeCmdToProcessed%@");
    NSLog(@"_dbg passed cmd_nid: %@");
    /*
    NSLog(@"%@", cmd_nid);
    NSLog(@"%@", cmd_nid);
    NSLog(@"%@", cmd_nid);
    NSLog(@"%@", cmd_nid);
    NSLog(@"%@", cmd_nid);
    NSLog(@"%@", cmd_nid);
    NSLog(@"%@", cmd_nid);
     */
    NSLog(@"_dbg exiting function %@");
    
    
}

-(void)logoffDrupal {
    
    NSString *requestString = [NSString stringWithFormat:@"",nil];
    NSLog(requestString);
    
    
    NSData *requestData = [NSData dataWithBytes: [requestString UTF8String] length: [requestString length]];
    
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL: [NSURL URLWithString: @"http://www.myrobot.com/drupal/rest/user/logout.json"]];
    
    NSString *postLength = [NSString stringWithFormat:@"%d", [requestData length]];
    [request setHTTPMethod: @"POST"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody: requestData];
    
    //Data returned by WebService
    NSData *returnData = [NSURLConnection sendSynchronousRequest: request returningResponse: nil error: nil ];
    NSString *returnString = [[NSString alloc] initWithData:returnData encoding: NSUTF8StringEncoding];
    
    NSLog(returnString);
    NSDictionary *result = [NSJSONSerialization JSONObjectWithData:[returnString dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    
   // [_session disconnect];
    //[_session unpublish:_publisher];
     [_session disconnect];
   // [_session unpublish:_publisher];

}





#pragma mark -- Private Methods: Build the UI --

- (void)layoutForConnected
{
    // Lets make some buttons so we can tell the robot to do stuff
    if (!self.connectedView) {
        self.connectedView = [[UIView alloc] initWithFrame:self.view.bounds];
        self.connectedView.backgroundColor = [UIColor whiteColor];
        
        self.driveInCircleButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        self.driveInCircleButton.frame = CGRectMake(70, 10, 180, 60);
        [self.driveInCircleButton setTitle:@"Drive in circle" forState:UIControlStateNormal];
        [self.driveInCircleButton addTarget:self action:@selector(didTouchDriveInCircleButton:) forControlEvents:UIControlEventTouchUpInside];
        [self.connectedView addSubview:self.driveInCircleButton];
        
        self.tiltDownButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        self.tiltDownButton.frame = CGRectMake(70, 80, 80, 60);
        [self.tiltDownButton setTitle:@"Tilt Up" forState:UIControlStateNormal];
        [self.tiltDownButton addTarget:self action:@selector(didTouchTiltUpButton:) forControlEvents:UIControlEventTouchUpInside];
        [self.connectedView addSubview:self.tiltDownButton];
        
        self.tiltUpButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        self.tiltUpButton.frame = CGRectMake(170, 80, 80, 60);
        [self.tiltUpButton setTitle:@"Tilt Down" forState:UIControlStateNormal];
        [self.tiltUpButton addTarget:self action:@selector(didTouchTiltDownButton:) forControlEvents:UIControlEventTouchUpInside];
        [self.connectedView addSubview:self.tiltUpButton];
        
        self.loginDrupalButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        self.loginDrupalButton.frame = CGRectMake(70, 290, 180, 60);
        
        [self.loginDrupalButton setTitle:@"Login Drupal" forState:UIControlStateNormal];
        [self.loginDrupalButton addTarget:self action:@selector(didTouchLoginDrupalButton:) forControlEvents:UIControlEventTouchUpInside];
        [self.connectedView addSubview:self.loginDrupalButton];
        
        /*
        self.logoffDrupalButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        self.logoffDrupalButton.frame = CGRectMake(70, 270, 180, 60);
        [self.logoffDrupalButton setTitle:@"Logoff Drupal" forState:UIControlStateNormal];
        [self.logoffDrupalButton addTarget:self action:@selector(didTouchLogoffDrupalButton:) forControlEvents:UIControlEventTouchUpInside];
        [self.connectedView addSubview:self.logoffDrupalButton];
         */
        
        /*
        self.getNewCmdsButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        self.getNewCmdsButton.frame = CGRectMake(70, 450, 180, 60);
        [self.getNewCmdsButton setTitle:@"New Cmds" forState:UIControlStateNormal];
        [self.getNewCmdsButton addTarget:self action:@selector(didTouchGetNewCmdsButton:) forControlEvents:UIControlEventTouchUpInside];
        [self.connectedView addSubview:self.getNewCmdsButton];
         */
        
        self.userNameTextField = [[UITextField alloc] initWithFrame:CGRectMake(70, 150, 180, 60)];
        self.userNameTextField.borderStyle = UITextBorderStyleRoundedRect;
        self.userNameTextField.font = [UIFont systemFontOfSize:15];
        self.userNameTextField.placeholder = @"enter username";
        self.userNameTextField.autocorrectionType = UITextAutocorrectionTypeNo;
        self.userNameTextField.keyboardType = UIKeyboardTypeDefault;
        self.userNameTextField.returnKeyType = UIReturnKeyDone;
        self.userNameTextField.clearButtonMode = UITextFieldViewModeWhileEditing;
        self.userNameTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.userNameTextField.delegate = self;
        [self.connectedView addSubview:self.userNameTextField];
        //[textField release];
        
        self.passwordTextField = [[UITextField alloc] initWithFrame:CGRectMake(70, 220, 180, 60)];
        self.passwordTextField.borderStyle = UITextBorderStyleRoundedRect;
        self.passwordTextField.font = [UIFont systemFontOfSize:15];
        self.passwordTextField.placeholder = @"enter password";
        self.passwordTextField.autocorrectionType = UITextAutocorrectionTypeNo;
        self.passwordTextField.keyboardType = UIKeyboardTypeDefault;
        self.passwordTextField.returnKeyType = UIReturnKeyDone;
        self.passwordTextField.clearButtonMode = UITextFieldViewModeWhileEditing;
        self.passwordTextField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
        self.passwordTextField.delegate = self;
        [self.connectedView addSubview:self.passwordTextField];
        //[textField release];
        
        
        
        self.goAvailableButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        self.goAvailableButton.frame = CGRectMake(70, 360, 180, 60);
        [self.goAvailableButton setTitle:@"Go Available" forState:UIControlStateNormal];
        [self.goAvailableButton addTarget:self action:@selector(didTouchGoAvailableButton:) forControlEvents:UIControlEventTouchUpInside];
        [self.connectedView addSubview:self.goAvailableButton];
        
        self.version_lbl =  [[UILabel alloc] initWithFrame: CGRectMake(10, 500, 300, 30)];
        self.version_lbl.text = @"Version 0.9.8"; //etc...
        self.version_lbl.textAlignment = UITextAlignmentCenter;
        //self.label.backgroundColor = [UIColor blackColor];
        self.version_lbl.textColor = [UIColor blackColor];
        [self.connectedView addSubview:self.version_lbl];
        
        self.label =  [[UILabel alloc] initWithFrame: CGRectMake(10, 530, 300, 30)];
        self.label.text = @"Not logged in"; //etc...
        self.label.textAlignment = UITextAlignmentCenter;
        self.label.backgroundColor = [UIColor blackColor];
        self.label.textColor = [UIColor whiteColor];
        [self.connectedView addSubview:self.label];
       // [label release];
        
        
       
        
    }
    
    [self.unconnectedView removeFromSuperview];
    [self.view addSubview:self.connectedView];
}

- (void)layoutForUnconnected
{
    // If we aren't connected to a robotic base, just show a label
    if (!self.unconnectedView) {
        self.unconnectedView = [[UIView alloc] initWithFrame:self.view.bounds];
        self.unconnectedView.backgroundColor = [UIColor whiteColor];
        
        UILabel *notConnectedLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, self.view.center.y, self.view.frame.size.width, 40)];
        notConnectedLabel.textAlignment = NSTextAlignmentCenter;
        notConnectedLabel.text = @"Romo Not Connected for MyRobot";
        [self.unconnectedView addSubview:notConnectedLabel];
    }

    [self.connectedView removeFromSuperview];
    [self.view addSubview:self.unconnectedView];
}

- (void)updateSubscriber {
    for (NSString* streamId in _session.streams) {
        OTStream* stream = [_session.streams valueForKey:streamId];
        if (![stream.connection.connectionId isEqualToString: _session.connection.connectionId]) {
            _subscriber = [[OTSubscriber alloc] initWithStream:stream delegate:self];
            break;
        }
    }
}

#pragma mark - OpenTok methods

- (void)doConnect
{
    [_session connectWithApiKey:kApiKey token:kToken];
}

- (void)doPublish
{
    _publisher = [[OTPublisher alloc] initWithDelegate:self];
    [_publisher setName:[[UIDevice currentDevice] name]];
    [_session publish:_publisher];
   }

- (void)sessionDidConnect:(OTSession*)session
{
    NSLog(@"sessionDidConnect (%@)", session.sessionId);
    [self doPublish];
}

- (void)sessionDidDisconnect:(OTSession*)session
{
    NSString* alertMessage = [NSString stringWithFormat:@"Session disconnected: (%@)", session.sessionId];
    NSLog(@"sessionDidDisconnect (%@)", alertMessage);
    [self showAlert:alertMessage];
}


- (void)session:(OTSession*)mySession didReceiveStream:(OTStream*)stream
{
    NSLog(@"session didReceiveStream (%@)", stream.streamId);
    
    // See the declaration of subscribeToSelf above.
    if ( (subscribeToSelf && [stream.connection.connectionId isEqualToString: _session.connection.connectionId])
        ||
        (!subscribeToSelf && ![stream.connection.connectionId isEqualToString: _session.connection.connectionId])
        ) {
        if (!_subscriber) {
            _subscriber = [[OTSubscriber alloc] initWithStream:stream delegate:self];
        }
    }
}

- (void)session:(OTSession*)session didDropStream:(OTStream*)stream{
    NSLog(@"session didDropStream (%@)", stream.streamId);
    NSLog(@"_subscriber.stream.streamId (%@)", _subscriber.stream.streamId);
    if (!subscribeToSelf
        && _subscriber
        && [_subscriber.stream.streamId isEqualToString: stream.streamId])
    {
        _subscriber = nil;
        [self updateSubscriber];
    }
}

- (void)session:(OTSession *)session didCreateConnection:(OTConnection *)connection {
    NSLog(@"session didCreateConnection (%@)", connection.connectionId);
}

- (void) session:(OTSession *)session didDropConnection:(OTConnection *)connection {
    NSLog(@"session didDropConnection (%@)", connection.connectionId);
}

- (void)subscriberDidConnectToStream:(OTSubscriber*)subscriber
{
    NSLog(@"subscriberDidConnectToStream (%@)", subscriber.stream.connection.connectionId);
    [subscriber.view setFrame:CGRectMake(0, widgetHeight, widgetWidth, widgetHeight)];
    [self.view addSubview:subscriber.view];
}

- (void)publisher:(OTPublisher*)publisher didFailWithError:(OTError*) error {
    NSLog(@"publisher didFailWithError %@", error);
    [self showAlert:[NSString stringWithFormat:@"There was an error publishing."]];
}

- (void)subscriber:(OTSubscriber*)subscriber didFailWithError:(OTError*)error
{
    NSLog(@"subscriber %@ didFailWithError %@", subscriber.stream.streamId, error);
    [self showAlert:[NSString stringWithFormat:@"There was an error subscribing to stream %@", subscriber.stream.streamId]];
}

- (void)session:(OTSession*)session didFailWithError:(OTError*)error {
    NSLog(@"sessionDidFail");
    [self showAlert:[NSString stringWithFormat:@"There was an error connecting to session %@", session.sessionId]];
}


- (void)showAlert:(NSString*)string {
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Message from video session"
                                                    message:string
                                                   delegate:self
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
}


- (IBAction)startTimer:(id)sender
{
    if (_timer == nil)
    {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1.0f
                                                  target:self
                                                selector:@selector(_timerFired)
                                                userInfo:nil
                                                 repeats:YES];
    }
}

- (IBAction)stopTimer:(id)sender
{
    if (_timer != nil)
    {
        [_timer invalidate];
        _timer = nil;
    }
}

- (void)_timerFired
{
    if(!isAvailable) {
        
        [self stopTimer:nil];
    } else {
      NSLog(@"ping");
      [self getNewCmds];
    }
}


- (IBAction)startMoveTimer:(id)sender
{
    if (_move_timer == nil)
    {
        _move_timer = [NSTimer scheduledTimerWithTimeInterval:1.0f
                                                  target:self
                                                selector:@selector(_moveTimerFired)
                                                userInfo:nil
                                                 repeats:NO];
    }
}

/*

- (IBAction)stopMoveTimer:(id)sender
{
    if (_timer != nil)
    {
        [_timer invalidate];
        _timer = nil;
    }
}
 */

- (void)_moveTimerFired
{
    NSLog(@"ping move timer");
    if (self.robot.isDriving) {
        // Change the robot's LED to be solid at 80% power
        [self.robot.LEDs setSolidWithBrightness:0.8];
        
        // Tell the robot to stop
        [self.robot stopDriving];
    }

   

if (_move_timer != nil)
{
    [_move_timer invalidate];
    _move_timer = nil;
}
}



@end
