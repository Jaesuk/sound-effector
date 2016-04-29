//
// Created by Jaesuk Hwang on 2016. 4. 26..
// Copyright (c) 2016 Jaesuk Hwang. All rights reserved.
//

import Foundation
import Alamofire
import PKHUD
import FBSDKCoreKit
import FBSDKLoginKit

protocol LoginManagerIsSessionAliveDelegate {
    func isSessionAliveTrue()

    func isSessionAliveFalse()
}

protocol LoginManagerLoginUsingFacebookDelegate {
    func loginUsingFacebookSuccess()

    func loginUsingFacebookConfirmingNeeded()

    func loginUsingFacebookFailure()
}

class LoginManager {
    // MARK: Properties
    static let sharedInstance = LoginManager()

    static let IS_SESSION_ALIVE_URL_STRING = "https://evening-inlet-23126.herokuapp.com/auth"
    static let IS_SESSION_ALIVE_HEADERS = ["Accept": "application/json"]
    static let LOGIN_USING_FACEBOOK_URL_STRING = "https://evening-inlet-23126.herokuapp.com/auth/facebook/app"
    static let LOGIN_USING_FACEBOOK_HEADERS = ["Accept": "application/json"]

    let httpCookieStorage = NSHTTPCookieStorage.sharedHTTPCookieStorage()
    let alamofireManager: Manager!

    let facebookSDKLoginManager: FBSDKLoginManager = FBSDKLoginManager()

    internal init() {
        let urlSessionConfiguration = NSURLSessionConfiguration.defaultSessionConfiguration()
        urlSessionConfiguration.HTTPAdditionalHeaders = Manager.defaultHTTPHeaders
        urlSessionConfiguration.HTTPCookieStorage = httpCookieStorage

        alamofireManager = Manager(configuration: urlSessionConfiguration)
    }

    internal func isSessionAlive(delegate: LoginManagerIsSessionAliveDelegate!) {
        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
        HUD.show(.Progress)

        alamofireManager.request(.HEAD, LoginManager.IS_SESSION_ALIVE_URL_STRING,
                headers: LoginManager.IS_SESSION_ALIVE_HEADERS)
        .response {
            request, response, data, error in
            UIApplication.sharedApplication().networkActivityIndicatorVisible = false
            HUD.hide(animated: false)

            if error == nil {
                switch response!.statusCode {
                case 200:
                    print("Session is alive.")
                    delegate.isSessionAliveTrue()
                    return
                case 403:
                    print("Session is not alive or valid")
                    delegate.isSessionAliveFalse()
                    return
                default:
                    print("Session... no way.")
                    delegate.isSessionAliveFalse()
                    return
                }
            } else {
                print("Our server may be sleeping... Error: \(error!.localizedDescription)")
                delegate.isSessionAliveFalse()
                return
            }
        }
    }

    internal func loginUsingFacebook(delegate: LoginManagerLoginUsingFacebookDelegate!,
                                   fromViewController: UIViewController!) {
        facebookSDKLoginManager.logInWithReadPermissions(["public_profile", "email"],
                fromViewController: fromViewController,
                handler: {
                    (result, error) -> Void in
                    print("Facebook login...")

                    if (error != nil) {
                        HUD.flash(.Label(error.localizedDescription), delay: 0.3) {
                            _ in
                            print("- error : \(error.localizedDescription)")
                        }
                        return
                    } else if result.isCancelled {
                        HUD.flash(.Label("Facebook login cancelled."), delay: 0.3) {
                            _ in
                            print("- cancelled")
                        }
                        return
                    } else {
                        print("- Facebook Access Token : \(FBSDKAccessToken.currentAccessToken().tokenString)")

                        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
                        HUD.show(.Progress)

                        let parameters = ["access-token": FBSDKAccessToken.currentAccessToken().tokenString]

                        self.alamofireManager.request(.GET, LoginManager.LOGIN_USING_FACEBOOK_URL_STRING,
                                headers: LoginManager.LOGIN_USING_FACEBOOK_HEADERS,
                                parameters: parameters)
                        .responseJSON {
                            response in
                            UIApplication.sharedApplication().networkActivityIndicatorVisible = false
                            HUD.hide(animated: false)

                            switch response.result {
                            case .Success(let JSON):
                                switch response.response!.statusCode {
                                case 200:
                                    print("- Login completed.")
                                    delegate.loginUsingFacebookSuccess()
                                case 202:
                                    print("- Confirming to join needed.")
                                    delegate.loginUsingFacebookConfirmingNeeded()
                                default:
                                    print("- Login... no way.")
                                    delegate.loginUsingFacebookFailure()
                                }

                                let result = JSON as! NSDictionary
                                HUD.flash(.Label(result["message"] as? String), delay: 0.3) {
                                    _ in
                                    print(result["message"] as! String)
                                }
                            case .Failure(let error):
                                print("Our server may be sleeping... Error: \(error.localizedDescription)")
                                delegate.loginUsingFacebookFailure()
                            }
                        }
                    }
                })
    }
}
