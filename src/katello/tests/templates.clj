(ns katello.tests.templates
  (:refer-clojure :exclude [fn])
  (:require (katello [tasks :as tasks]
                     [validation :as v]
                     [api-tasks :as api])
            [clj-http.client :as http]
            [clojure.java.io :as io])
  (:use [test.tree.builder :only [fn data-driven]]
        [com.redhat.qe.verify :only [verify-that]]
        [com.redhat.qe.auto.bz :only [open-bz-bugs]]
        [katello.conf :only [config]]))

(def test-template-name (atom nil))
(def content (atom nil))
(def products (atom []))
(def create
  (fn []
    (tasks/create-template {:name (reset! test-template-name (tasks/uniqueify "template"))
                            :description "my test template"})))

(def setup-content
  (fn []
    (let [provider-name (tasks/uniqueify "template")]
      (api/with-admin
        (api/create-provider provider-name))
      (reset! products (tasks/uniqueify "templateProduct" 3 ))
        (for [product @products]
          (api/with-admin
            (api/create-product product {:provider-name provider-name
                                         :description "product to test templates"}))))))

(def add-content
  (fn []
    (tasks/add-to-template @test-template-name {:products @products})))