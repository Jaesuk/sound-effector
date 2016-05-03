//
// Created by Jaesuk Hwang on 2016. 4. 29..
// Copyright (c) 2016 Jaesuk Hwang. All rights reserved.
//

import Foundation
import UIKit
import PKHUD

class LoginViewController: UIViewController, LoginManagerLoginUsingFacebookDelegate {
    @IBAction func facebookButtonPressed(sender: AnyObject) {
        LoginManager.sharedInstance.loginUsingFacebook(self, fromViewController: self)
    }

    func loginUsingFacebookSuccess() {
        let viewController = storyboard!.instantiateViewControllerWithIdentifier("viewController") as UIViewController
        presentViewController(viewController, animated: true, completion: nil)
    }

    func loginUsingFacebookConfirmingNeeded(confirmingInfo: LoginUsingFacebookConfirmingInfo!) {
        let joinViewController = storyboard!.instantiateViewControllerWithIdentifier("joinViewController") as! JoinViewController
        joinViewController.confirmingInfo = confirmingInfo
        presentViewController(joinViewController, animated: true, completion: nil)
    }

    func loginUsingFacebookFailure() {
        HUD.flash(.Label("Facebook login failed..."), delay: 0.3) {
            _ in
            print("Facebook login failed...")
        }
    }
}
