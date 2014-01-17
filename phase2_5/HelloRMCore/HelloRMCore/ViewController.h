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
//  ViewController.h
//  HelloRMCore
//
//  Created by Romotive on 5/21/13.
//  Copyright (c) 2013 Romotive. All rights reserved.
//
//==============================================================================
#import <UIKit/UIKit.h>
#import <Opentok/Opentok.h>
#import <RMCore/RMCore.h>


@interface ViewController : UIViewController <RMCoreDelegate, OTSessionDelegate, OTPublisherDelegate, UITextFieldDelegate>




@property (nonatomic, strong) RMCoreRobot<HeadTiltProtocol, DriveProtocol, LEDProtocol> *robot;

@property (nonatomic, strong) NSString *sessid;
@property (nonatomic, strong) NSString *sess_name;
@property (nonatomic, strong) NSString *cellbot_nid;

// UI
@property (nonatomic, strong) UIView *connectedView;
@property (nonatomic, strong) UILabel *batteryLabel;
@property (nonatomic, strong) UIButton *driveInCircleButton;

@property (nonatomic, strong) UILabel *label;
@property (nonatomic, strong) UILabel *version_lbl;
@property (nonatomic, strong) UIButton *tiltUpButton;
@property (nonatomic, strong) UIButton *tiltDownButton;

@property (strong, nonatomic) IBOutlet UITextField *userNameTextField;

@property (strong, nonatomic) IBOutlet UITextField *passwordTextField;

@property (nonatomic, strong) UIButton *loginDrupalButton;

@property (nonatomic, strong) UIButton *logoffDrupalButton;

@property (nonatomic, strong) UIButton *getNewCmdsButton;

@property (nonatomic, strong) IBOutlet UIButton *goAvailableButton;

@property (nonatomic, strong) UIView *unconnectedView;

- (void)didTouchDriveInCircleButton:(UIButton *)sender;
- (void)didTouchTiltDownButton:(UIButton *)sender;
- (void)didTouchTiltUpButton:(UIButton *)sender;
- (void)didTouchLoginDrupalButton:(UIButton *)sender;
- (void)didTouchLogoffDrupalButton:(UIButton *)sender;
- (void)didTouchGetNewCmdsButton:(UIButton *)sender;
- (void)didTouchGoAvailableButton:(UIButton *)sender;

@end
