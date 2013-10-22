<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Verbs extends MY_Controller {

    public function __construct() {
        parent::__construct();
    }

    public function index() {
        header('Content-Type: text/plain;charset=UTF-8');
        $this->load->model('Verb_model');
        foreach ($this->Verb_model->getall() as $row)
            echo $row["verb"];
    }

    public function getverb($name) {
        header('Content-Type: text/plain;charset=UTF-8');
        $this->load->model('Verb_model');
        echo $this->Verb_model->get($name);
    }

    public function load() {
        header('Content-Type: text/plain;charset=UTF-8');
        $this->load->model('Verb_model');

        $file = @fopen('verbi.txt', "r");
        $i = 0;

        while (!feof($file)) {
            // Get the current line that the file is reading
            $currentLine = fgets($file);
            if (($i % 96) == 0) {
                if ($this->Verb_model->verb) {
                    $this->Verb_model->insert();
                    echo $this->Verb_model->verb;
                }
                $this->Verb_model->verb = trim($currentLine);
                $this->Verb_model->content = "";
            } else {
                $this->Verb_model->content .= $currentLine;
            }
            $i++;
        }

        fclose($file);
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */