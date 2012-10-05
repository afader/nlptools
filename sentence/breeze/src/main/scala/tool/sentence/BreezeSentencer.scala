package edu.washington.cs.knowitall
package tool
package sentence

import breeze.text.segment.SentenceSegmenter
import breeze.text.segment.JavaSentenceSegmenter

class BreezeSentencer extends Sentencer {
  val sentencer = new JavaSentenceSegmenter()

  def sentences(document: String) = sentencer(document)
}

object BreezeSentencerMain
extends SentencerMain {
  lazy val sentencer = new BreezeSentencer
}
