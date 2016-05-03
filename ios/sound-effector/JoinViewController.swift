//
// Created by Jaesuk Hwang on 2016. 4. 30..
// Copyright (c) 2016 Jaesuk Hwang. All rights reserved.
//

import Foundation
import UIKit
import AlamofireImage
import PKHUD

class JoinViewController: UIViewController {
    // MARK: Properties
    var confirmingInfo: LoginUsingFacebookConfirmingInfo!
    @IBOutlet weak var profileImageView: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()

        // FIXME: There is the way to use SDK... instead of...
        let profileUrlString = "https://graph.facebook.com/\(confirmingInfo.providerUserId)/picture?type=square&width=100&height=100"
        LoginManager.sharedInstance.alamofireManager
        .request(.GET, profileUrlString)
        .responseImage {
            response in

            if let image = response.result.value {
                self.profileImageView.image = image
            }
        }
        nameLabel.text = confirmingInfo.name
        emailLabel.text = confirmingInfo.email
    }

    @IBAction func cancelButtonPressed(sender: AnyObject) {
        let loginViewController = storyboard!.instantiateViewControllerWithIdentifier("loginViewController") as UIViewController
        presentViewController(loginViewController, animated: true, completion: nil)
    }

    @IBAction func confirmButtonPressed(sender: AnyObject) {
        let urlString = "https://evening-inlet-23126.herokuapp.com/users"
        let headers = ["Accept": "application/json",
                       "X-CSRF-Token": confirmingInfo.csrfToken]

        LoginManager.sharedInstance.alamofireManager
        .request(.POST, urlString, headers: headers)
        .responseJSON {
            response in
            UIApplication.sharedApplication().networkActivityIndicatorVisible = false
            HUD.hide(animated: false)

            switch response.result {
            case .Success(let JSON):
                if response.response!.statusCode == 201 {
                    HUD.flash(.Label("Joined! :D"), delay: 0.3) {
                        _ in
                        print("Joined! :D")
                        let viewController = self.storyboard!.instantiateViewControllerWithIdentifier("viewController") as UIViewController
                        self.presentViewController(viewController, animated: true, completion: nil)
                    }
                } else {
                    HUD.flash(.Label("Couldn't joing... :("), delay: 0.3) {
                        _ in
                        print("Couldn't joing... :(\n\t\(response.response!)")
                    }
                }
            case .Failure(let error):
                print("Our server may be sleeping... Error: \(error.localizedDescription)")
            }
        }
    }
}
