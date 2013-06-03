<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Crypt extends MY_Controller {

    var $scripts;

    const CryptIndex = "CryptIndex";

    public function __construct() {
        parent::__construct();


        $s1 = "this.crypt = function(s){\n" . "    var ret = '';\n"
                . "    for (var i = 0; i < s.length; i++){\n"
                . "        var code = s.charCodeAt(i) + 1;\n"
                . "        ret += String.fromCharCode(code);\n"
                . "    }\n"
                . "    return ret;"
                . "}"; //************************************************************
        $s2 = "this.crypt = function(s){\n"
                . "    var ret = '';\n"
                . "    for (var i = 0; i < s.length; i++){\n"
                . "        var code = s.charCodeAt(i) -1;\n"
                . "        ret += String.fromCharCode(code);\n"
                . "    }\n"
                . "    return ret;\n"
                . "}"; //**************************************************************
        $s3 =
                "this.crypt = function(s){\n"
                . "    var ret = '';\n"
                . "    for (var i = 0; i < s.length; i=i+2){\n"
                . "        ret += s.charAt(i);\n"
                . "    }\n"
                . "    for (var i = 1; i < s.length; i=i+2){\n"
                . "        ret += s.charAt(i);\n"
                . "    }\n"
                . "    return ret;\n"
                . "}"; //**************************************************************
        $s4 = "this.crypt = function(s){\n"
                . "    var ret = '';\n"
                . "    for (var i = 1; i < s.length; i=i+2){\n"
                . "        ret += s.charAt(i);\n"
                . "    }\n"
                . "    for (var i = 0; i < s.length; i=i+2){\n"
                . "        ret += s.charAt(i);\n"
                . "    }\n"
                . "    return ret;\n"
                . "}";

        $this->scripts = array($s1, $s2, $s3, $s4);
    }

    private function get_script() {
        $i = rand(0, count($this->scripts) - 1);

        $_SESSION[Crypt::CryptIndex] = $i;
        return $this->scripts[$i];
    }

    private function decrypt($str) {
        $index = $_SESSION[Crypt::CryptIndex];
        if (i == NULL) {
            throw "ERROR";
        }
        $ret = "";
        $strlen = strlen($str);

        switch ($index) {
            case 0: {
                    for ($i = 0; $i <= $strlen; $i++) {
                        $char = substr($str, $i, 1);
                        $char--;
                        $ret .= $char;
                    }
                    break;
                }
            case 1: {
                    for ($i = 0; $i <= $strlen; $i++) {
                        $char = substr($str, $i, 1);
                        $char++;
                        $ret .= $char;
                    }
                    break;
                }
            case 2: {
                    $mid = ceil($strlen / 2.0);

                    for ($j = 0; $j < mid; $j++) {
                        $ret .= substr($str, $j, 1);
                        $j2 = $j + $mid;
                        if ($j2 < $strlen) {
                            $ret .= substr($str, $j2, 1);
                        }
                    }
                    break;
                }
            case 3: {
                    $mid = floor($strlen / 2.0);
                    for ($j = $mid; $j < $strlen; $j++) {
                        $ret .= substr($str, $j, 1);
                        $j2 = $j - $mid;
                        if ($j2 < $mid) {
                            $ret .= substr($str, $j2, 1);
                        }
                    }
                    break;
                }
            default:
                throw "ERROR";
        }
        return $ret;
    }

    public function index() {
        header('Content-Type: text/plain;charset=UTF-8');
        echo $this->get_script();
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */