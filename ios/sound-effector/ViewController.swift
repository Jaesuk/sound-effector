//
// ViewController.swift
// sound-effector
//
// Start Developing iOS Apps (Swift)
// - https://developer.apple.com/library/ios/referencelibrary/GettingStarted/DevelopiOSAppsSwift/index.html#//apple_ref/doc/uid/TP40015214-CH2-SW1
//
// Created by Jaesuk Hwang on 2/22/16.
// Copyright (c) 2016 Jaesuk Hwang. All rights reserved.
//

import UIKit
import AVFoundation
import Alamofire
import PKHUD
import FBSDKCoreKit
import FBSDKLoginKit

class ViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, LoginManagerIsSessionAliveDelegate {
    // MARK: Properties
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var queryTextField: UITextField!
    @IBOutlet weak var searchButton: UIButton!
    @IBOutlet weak var allButton: UIButton!
    var refreshControl: UIRefreshControl!
    var soundEffects = [SoundEffect]()
    var audioPlayer: AVAudioPlayer = AVAudioPlayer()

    override func viewDidLoad() {
        super.viewDidLoad()

        tableView.delegate = self
        tableView.dataSource = self

        refreshControl = UIRefreshControl()
        refreshControl.attributedTitle = NSAttributedString(string: "Pull to refresh")
        refreshControl.addTarget(self, action: #selector(ViewController.refresh(_:)), forControlEvents: UIControlEvents.ValueChanged)
        tableView.addSubview(refreshControl)
    }

    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)

        // TODO: Don't need to do this every time.
        LoginManager.sharedInstance.isSessionAlive(self)
    }

    func isSessionAliveTrue() {
        loadSoundEffects()
    }

    func isSessionAliveFalse() {
        let viewController = storyboard!.instantiateViewControllerWithIdentifier("viewController") as UIViewController
        presentViewController(viewController, animated: true, completion: nil)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    func loadSoundEffects() {
        loadSoundEffects(nil)
    }

    func loadSoundEffects(query: String?) {
        UIApplication.sharedApplication().networkActivityIndicatorVisible = true
        HUD.show(.Progress)
        soundEffects.removeAll(keepCapacity: true)

        let urlString = "https://evening-inlet-23126.herokuapp.com/sound-effects"
        let headers = ["Accept": "application/json"]
        var parameters = [String: String]()
        if query != nil && query!.isEmpty == false {
            parameters["q"] = query!
        }

        Alamofire.Manager.sharedInstance.request(.GET, urlString, headers: headers, parameters: parameters)
        .responseJSON {
            response in
            UIApplication.sharedApplication().networkActivityIndicatorVisible = false
            HUD.hide(animated: false)

            switch response.result {
            case .Success(let JSON):
                if JSON is NSArray {
                    let resultArray = JSON as! NSArray
                    for result in resultArray {
                        let resultDictionary = result as! NSDictionary
                        self.soundEffects +=
                                [SoundEffect(title: resultDictionary["title"] as! String,
                                        url: resultDictionary["url"] as! String)]
                    }

                    self.tableView.reloadData()
                } else if JSON is NSDictionary {
                    let result = JSON as! NSDictionary
                    HUD.flash(.Label(result["message"] as? String), delay: 0.3) {
                        _ in
                        print(result["message"] as! String)
                    }
                }
            case .Failure(let error):
                print("Our server may be sleeping... Error: \(error.localizedDescription)")
            }
        }
    }

    func refresh(sender: AnyObject) {
        loadSoundEffects()
        refreshControl.endRefreshing()
    }

    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }

    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return soundEffects.count
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cellIdentifier = "SoundEffectTableViewCell"
        let cell = tableView.dequeueReusableCellWithIdentifier(cellIdentifier, forIndexPath: indexPath) as! SoundEffectTableViewCell

        let soundEffect = soundEffects[indexPath.row]

        cell.titleLabel.text = soundEffect.title
        cell.urlLabel.text = soundEffect.url

        return cell
    }

    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let soundEffect = soundEffects[indexPath.row]
        let soundEffectUrl = NSURL(string: soundEffect.url)
        let soundEffectData = NSData(contentsOfURL: soundEffectUrl!)

        do {
            audioPlayer = try AVAudioPlayer(data: soundEffectData!)
            audioPlayer.prepareToPlay()
            audioPlayer.play()
        } catch {
            print("Couldn't play the sound effect at '\(soundEffect.url)'.")
        }
    }

    @IBAction func searchButtonPressed(sender: AnyObject) {
        if queryTextField.text == nil || queryTextField.text?.isEmpty == true {
            HUD.flash(.Label("Please type the keyword to search..."), delay: 0.3) {
                _ in
                print("queryTextField is nil or empty.")
            }

            return
        }

        loadSoundEffects(queryTextField.text)
    }

    @IBAction func allButtonPressed(sender: AnyObject) {
        loadSoundEffects()
    }
}
