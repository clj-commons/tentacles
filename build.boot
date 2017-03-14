(set-env!
 :source-paths   #{"src"}
 :resource-paths #{"src" "resources"}
 :dependencies '[[org.clojure/clojure "1.8.0"]
                 [clj-http "3.4.1"]
                 [cheshire "5.7.0"]
                 [com.cemerick/url "0.1.1"]
                 [org.clojure/data.codec "0.1.0"]
                 [environ "1.1.0"]
                 [adzerk/boot-test "1.2.0"]]
  :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"
                                   :username (System/getenv "CLOJARS_USER")
                                   :password (System/getenv "CLOJARS_PASS")
  }])
)
 
(require '[adzerk.boot-test :as t])

(task-options!
 pom '{:project irresponsible/tentacles
       :version "0.6.0"
       :description "A library for working with the Github API."
       :url "https://github.com/irresponsible/tentacles"
       :license {"Eclipse Public License" "http://www.eclipse.org/legal/epl-v10.html"}}
 push {:tag true
       :ensure-branch "master"
       :ensure-release true
       :ensure-clean true
       :gpg-sign true
       :repo "clojars"})

(deftask testing []
  (set-env! :source-paths   #(conj % "test")
            :resource-paths #(conj % "test"))
  identity)

(deftask test []
  (testing)
  (t/test))

(deftask autotest []
  (testing)
  (comp (watch) (t/test)))

(deftask installdeps []
  identity)

;; RMG Only stuff
(deftask make-jar []
  (comp (pom) (jar) (target)))

(deftask install-jar []
  (comp (pom) (jar) (install)))

(deftask release []
  (comp (pom) (jar) (push)))

;; Travis Only stuff
(deftask travis []
  (testing)
  (comp (t/test) (make-jar)))

(deftask travis-installdeps []
  (testing) identity)

(deftask jitpak-deploy []
  (task-options! pom {
    :project (symbol (System/getenv "ARTIFACT"))
  })
  (comp
    (pom)
    (jar)
    (target)      ; Must install to build dir
    (install)     ; And to .m2 https://jitpack.io/docs/BUILDING/#build-customization
  ))
