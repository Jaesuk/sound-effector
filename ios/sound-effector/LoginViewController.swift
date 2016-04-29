//
// Created by Jaesuk Hwang on 2016. 4. 29..
// Copyright (c) 2016 Jaesuk Hwang. All rights reserved.
//

import Foundation
import UIKit

class LoginViewController: UIViewController, LoginManagerLoginUsingFacebookDelegate {
    @IBAction func facebookButtonPressed(sender: AnyObject) {
        LoginManager.sharedInstance.loginUsingFacebook(self, fromViewController: self)
    }

    func loginUsingFacebookSuccess() {
        let viewController = storyboard!.instantiateViewControllerWithIdentifier("viewController") as UIViewController
        presentViewController(viewController, animated: true, completion: nil)
    }

    func loginUsingFacebookConfirmingNeeded() {

    }

    func loginUsingFacebookFailure() {

    }
}
