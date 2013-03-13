import sbt._
import Keys._
object NlpToolsBuild extends Build {
  lazy val root = Project(id = "nlptools", base = file(".")) aggregate(core,
    opennlpSentence, opennlpTokenize, opennlpPostag, opennlpChunk, opennlpParse,
    stanfordTokenize, stanfordPostag, stanfordParse, stanfordCoref,
    clearTokenize, clearPostag, clearParse, clearSrl,
    breezeTokenize, breezeSentence, breezeConf,
    morphaStemmer, snowballStemmer)


  // settings
  val scalaVersions = Seq("2.9.2", "2.10.0")
  override lazy val settings = super.settings ++ Seq(
    crossScalaVersions := scalaVersions
  )


  // license helpers
  val apache2 = "Apache 2.0 " -> url("http://www.opensource.org/licenses/bsd-3-clause")
  val gpl2 = "GPL 2.0 " -> url("http://www.gnu.org/licenses/gpl-2.0.html")


  // dependency helpers
  val opennlp = "org.apache.opennlp" % "opennlp-tools" % "1.5.2-incubating"

  val stanfordModelGroup = "edu.washington.cs.knowitall.stanford-corenlp"
  val stanfordVersion = "1.3.4"
  val stanford = "edu.stanford.nlp" % "stanford-corenlp" % stanfordVersion

  val clearModelGroup = "edu.washington.cs.knowitall.clearnlp"
  val clearVersion = "1.3.0"
  val clear = "com.googlecode.clearnlp" % "clearnlp" % clearVersion


  // dependencies
  val junit = "junit" % "junit" % "4.11"
  val commonScala = "edu.washington.cs.knowitall.common-scala" %% "common-scala" % "1.1.0"
  val specs2 = "org.specs2" %% "specs2" % "1.12.3"
  val scopt = "com.github.scopt" %% "scopt" % "2.1.0"
  val slf4j = "org.slf4j" % "slf4j-api" % "1.7.2"
  val unfilteredFilter = "net.databinder" %% "unfiltered-filter" % "0.6.5"
  val unfilteredJetty = "net.databinder" %% "unfiltered-jetty" % "0.6.5"


  // parent build definition
  val buildOrganization = "edu.washington.cs.knowitall.nlptools"
  val buildVersion = "2.4.0-SNAPSHOT"
  val buildScalaVersion = "2.9.2"
  val buildSettings = Defaults.defaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    crossScalaVersions := scalaVersions,
    scalaVersion <<= (crossScalaVersions) { versions => versions.head },
    libraryDependencies ++= Seq(junit % "test", specs2 % "test", unfilteredFilter % "provided", unfilteredJetty % "provided"),
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    homepage := Some(url("https://github.com/knowitall/nlptools")),
    pomExtra := (
      <scm>
        <url>https://github.com/knowitall/nlptools</url>
        <connection>scm:git://github.com/knowitall/nlptools.git</connection>
        <developerConnection>scm:git:git@github.com:knowitall/nlptools.git</developerConnection>
        <tag>HEAD</tag>
      </scm>
      <developers>
       <developer>
          <name>Michael Schmitz</name>
        </developer>
      </developers>))

  // Core

  lazy val core = Project(id = "nlptools-core", base = file("core"), settings = buildSettings ++ Seq(
    licenses := Seq(apache2),
    libraryDependencies ++= Seq(commonScala, scopt, slf4j)
  ))

  // OpenNLP

  lazy val opennlpSentence = Project(
    id = "nlptools-sentence-opennlp",
    base = file("sentence/opennlp"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      (libraryDependencies ++= Seq(opennlp, "edu.washington.cs.knowitall" % "opennlp-sent-models" % "1.5" )))
  ) dependsOn(core)

  lazy val opennlpTokenize = Project(
    id = "nlptools-tokenize-opennlp",
    base = file("tokenize/opennlp"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(opennlp, "edu.washington.cs.knowitall" % "opennlp-tokenize-models" % "1.5" ))
  ) dependsOn(core)


  lazy val opennlpPostag = Project(
    id = "nlptools-postag-opennlp",
    base = file("postag/opennlp"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(opennlp, "edu.washington.cs.knowitall" % "opennlp-postag-models" % "1.5" ))
  ) dependsOn(opennlpTokenize)

  lazy val opennlpChunk = Project(
    id = "nlptools-chunk-opennlp",
    base = file("chunk/opennlp"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(opennlp, "edu.washington.cs.knowitall" % "opennlp-chunk-models" % "1.5" ))
  ) dependsOn(opennlpPostag)

  lazy val opennlpParse = Project(
    id = "nlptools-parse-opennlp",
    base = file("parse/opennlp"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(opennlp, "edu.washington.cs.knowitall" % "opennlp-parse-models" % "1.5" ))
  ) dependsOn(opennlpPostag)

  // Stanford

  lazy val stanfordTokenize = Project(
    id = "nlptools-tokenize-stanford",
    base = file("tokenize/stanford"),
    settings = buildSettings ++ Seq(
      licenses := Seq(gpl2),
      libraryDependencies ++= Seq(stanford))
  ) dependsOn(core)

  lazy val stanfordPostag = Project(
    id = "nlptools-postag-stanford",
    base = file("postag/stanford"),
    settings = buildSettings ++ Seq(
      licenses := Seq(gpl2),
      libraryDependencies ++= Seq(stanford, stanfordModelGroup % "stanford-postag-models" % stanfordVersion ))
  ) dependsOn(stanfordTokenize)

  lazy val stanfordParse = Project(
    id = "nlptools-parse-stanford",
    base = file("parse/stanford"),
    settings = buildSettings ++ Seq(
      licenses := Seq(gpl2),
      libraryDependencies ++= Seq(stanford, stanfordModelGroup % "stanford-parse-models" % stanfordVersion ))
  ) dependsOn(stanfordPostag)

  lazy val stanfordCoref = Project(
    id = "nlptools-coref-stanford",
    base = file("coref/stanford"),
    settings = buildSettings ++ Seq(
      licenses := Seq(gpl2),
      libraryDependencies ++= Seq(stanford,
        stanfordModelGroup % "stanford-sutime-models" % stanfordVersion ,
        stanfordModelGroup % "stanford-ner-models" % stanfordVersion ,
        stanfordModelGroup % "stanford-dcoref-models" % stanfordVersion, scopt ))
  ) dependsOn(stanfordParse)

/*
  lazy val stanfordTyper = Project(
    id = "nlptools-typer-stanford",
    base = file("typer/stanford"),
    settings = buildSettings ++ Seq(
      libraryDependencies ++= Seq(stanford, stanfordModelGroup % "stanford-ner-models" % stanfordVersion ))
  ) dependsOn(core)
*/

  // Clear

  lazy val clearTokenize = Project(
    id = "nlptools-tokenize-clear",
    base = file("tokenize/clear"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(clear))
  ) dependsOn(core)

  lazy val clearPostag = Project(
    id = "nlptools-postag-clear",
    base = file("postag/clear"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(clear, clearModelGroup % "clear-postag-models" % clearVersion ))
  ) dependsOn(clearTokenize)

  lazy val clearParse = Project(
    id = "nlptools-parse-clear",
    base = file("parse/clear"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(clear, clearModelGroup % "clear-parse-models" % clearVersion ))
  ) dependsOn(clearPostag)

  lazy val clearSrl = Project(
    id = "nlptools-srl-clear",
    base = file("srl/clear"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(
        clear,
        clearModelGroup % "clear-role-models" % clearVersion,
        clearModelGroup % "clear-pred-models" % clearVersion,
        clearModelGroup % "clear-srl-models" % clearVersion))
  ) dependsOn(clearParse)

  // Breeze

  lazy val breezeTokenize = Project(
    id = "nlptools-tokenize-breeze",
    base = file("tokenize/breeze"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(clear,
        "org.scalanlp" %% "breeze-process" % "0.1"))
  ) dependsOn(core)

  lazy val breezeSentence = Project(
    id = "nlptools-sentence-breeze",
    base = file("sentence/breeze"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(clear,
        "org.scalanlp" %% "breeze-process" % "0.1"))
  ) dependsOn(core)

  lazy val breezeConf = Project(
    id = "nlptools-conf-breeze",
    base = file("conf/breeze"),
    settings = buildSettings ++ Seq(
      licenses := Seq(apache2),
      libraryDependencies ++= Seq(clear,
        "org.scalanlp" %% "breeze-process" % "0.1",
        "org.scalanlp" %% "breeze-learn" % "0.1"))
  ) dependsOn(core)

  // Stemmers

  lazy val morphaStemmer = Project(
    id = "nlptools-stem-morpha",
    base = file("stem/morpha"),
    settings = buildSettings ++ Seq(
      licenses := Seq(
        "Academic License (for original lex files)" -> url("http://www.informatics.sussex.ac.uk/research/groups/nlp/carroll/morph.tar.gz"),
        "Apache 2.0 (for supplemental code)" -> url("http://www.opensource.org/licenses/bsd-3-clause")),
      libraryDependencies ++= Seq(clear,
        "edu.washington.cs.knowitall" % "morpha-stemmer" % "1.0.4"))
  ) dependsOn(core)

  lazy val snowballStemmer = Project(
    id = "nlptools-stem-snowball",
    base = file("stem/snowball"),
    settings = buildSettings ++ Seq(
      licenses := Seq("BSD" -> url("http://snowball.tartarus.org/license.php")),
      libraryDependencies ++= Seq(clear,
        "org.apache.lucene" % "lucene-snowball" % "3.0.3"))
  ) dependsOn(core)
}