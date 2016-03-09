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

class ViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {
    // MARK: Properties
    @IBOutlet weak var tableView: UITableView!
    var soundEffects = [SoundEffect]()
    var audioPlayer: AVAudioPlayer = AVAudioPlayer()

    override func viewDidLoad() {
        super.viewDidLoad()

        self.tableView.delegate = self
        self.tableView.dataSource = self

        loadSoundEffects()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    func loadSoundEffects() {
        Alamofire.request(.GET, "http://evening-inlet-23126.herokuapp.com/api/sound-effects")
        .responseJSON {
            response in
            switch response.result {
            case .Success(let JSON):
                let resultArray = JSON as! NSArray
                for result in resultArray {
                    let resultDictionary = result as! NSDictionary
                    self.soundEffects +=
                            [SoundEffect(title: resultDictionary["title"] as! String,
                                    url: resultDictionary["url"] as! String)]
                }

                self.tableView.reloadData()
            case .Failure(let error):
                print("Our server may be sleeping... Error: \(error)")
            }
        }
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
            self.audioPlayer = try AVAudioPlayer(data: soundEffectData!)
            audioPlayer.prepareToPlay()
            audioPlayer.play()
        } catch {
            print("Couldn't play the sound effect at '\(soundEffect.url)'.")
        }
    }
}
