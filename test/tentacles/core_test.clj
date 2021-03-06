(ns tentacles.core-test
  (:use clojure.test)
  (:require [tentacles.core :as core]))

(deftest patch-requests-encode-json-body
  (let [request (core/make-request :patch "foo" nil {:bar "baz"})]
    (is (= (:body request) "{\"bar\":\"baz\"}"))))

(deftest request-contains-user-agent
  (let [request (core/make-request :get "test" nil {:user-agent "Mozilla"})]
    (do
      (is (empty?    (:query-params request)))
      (is (contains? (:headers request) "User-Agent"))
      (is (= (get (:headers request) "User-Agent") "Mozilla")))))

(deftest request-handle-multiple-headers
  (let [request (core/make-request :get "test" nil {:user-agent "Mozilla"
                                                    :etag "sample-etag"})]
    (is (empty?    (:query-params request)))
    (is (contains? (:headers request) "User-Agent"))
    (is (contains? (:headers request) "If-None-Match"))
    (is (= (get (:headers request) "User-Agent") "Mozilla"))
    (is (= (get (:headers request) "If-None-Match") "sample-etag"))))

(deftest request-contains-user-agent-from-defaults
  (core/with-defaults {:user-agent "Mozilla"}
    (let [request (core/make-request :get "test" nil {})]
      (do
        (is (empty?    (:query-params request)))
        (is (contains? (:headers request) "User-Agent"))
        (is (= (get (:headers request) "User-Agent") "Mozilla"))))))

(deftest request-with-bearer-token
  (core/with-defaults {:bearer-token "test-token"}
    (let [request (core/make-request :get "test" nil {})]
      (is (= (get (:headers request) "Authorization") "Bearer test-token")))))

(deftest adhoc-options-override-defaults
  (core/with-defaults {:user-agent "default"}
    (let [request (core/make-request :get "test" nil {:user-agent "adhoc"})]
      (do
        (is (empty?    (:query-params request)))
        (is (contains? (:headers request) "User-Agent"))
        (is (= (get (:headers request) "User-Agent") "adhoc"))))))

(deftest hitting-rate-limit-is-propagated
  (is (= (:status (core/safe-parse {:status 403}))
         403)))

(deftest rate-limit-details-are-propagated
  (is (= 60 (:call-limit (core/api-meta
                          (core/safe-parse {:status 200 :headers {"x-ratelimit-limit" "60"
                                                                  "content-type" "application/json"}}))))))

(deftest poll-limit-details-are-propagated
  (is (= 61 (:poll-interval (core/api-meta
                             (core/safe-parse {:status 200
                                               :headers {"x-poll-interval" "61"
                                                         "content-type" "application/json"}}))))))

(deftest http-method-is-set 
  (let [request (core/make-request :get "test" nil {})]
    (is (= :get (:method request)))))

(deftest request-allows-conn-and-socket-timeouts
  (let [request (core/make-request :get "test" nil {})]
    (is (nil? (:connection-timeout request)))
    (is (nil? (:conn-timeout request)))
    (is (nil? (:socket-timeout request))))

  (let [request (core/make-request :get "test" nil {:socket-timeout 5000
                                                    :connection-timeout 3000
                                                    :conn-timeout 5000})]
    (is (nil? (-> request :query-params (get "socket_timeout"))))
    (is (nil? (-> request :query-params (get "connection_timeout"))))
    (is (nil? (-> request :query-params (get "conn_timeout"))))
    (is (= 5000 (:conn-timeout request)))
    (is (= 3000 (:connection-timeout request)))
    (is (= 5000 (:socket-timeout request)))))
