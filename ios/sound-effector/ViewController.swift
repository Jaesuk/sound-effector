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
        // addicts Noise - Sound Samples
        // - http://www.noiseaddicts.com/free-samples-mp3

        // Fart
        // - http://www.noiseaddicts.com/free-samples-mp3/?id=3694
        soundEffects += [SoundEffect(title: "Fart", url: "http://www.noiseaddicts.com/samples_1w72b820/3694.mp3")]
        // Snoring
        // - http://www.noiseaddicts.com/free-samples-mp3/?id=3725
        soundEffects += [SoundEffect(title: "Snoring", url: "http://www.noiseaddicts.com/samples_1w72b820/3725.mp3")]


        // SoundBible.com - Free Sound Effects
        // - http://soundbible.com/free-sound-effects-1.html

        // Coin Drop
        // - http://soundbible.com/2081-Coin-Drop.html
        soundEffects += [SoundEffect(title: "Coin Drop", url: "http://soundbible.com/mp3/Coin_Drop-Willem_Hunt-569197907.mp3")]
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
