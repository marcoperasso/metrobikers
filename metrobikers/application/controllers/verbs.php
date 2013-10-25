<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Verbs extends MY_Controller {

    public function __construct() {
        parent::__construct();
    }

    public function index() {
        header('Content-Type: text/html;charset=UTF-8');
        $this->load->model('Verb_model');
		foreach ($this->Verb_model->getall() as $row)
		{
            echo '<a href="verbs/get/' . base64_encode($row["verb"]) . '">' . $row["verb"] . '</a><br/>';
			echo "\r\n";
		}
    }
	public function gethints($constraint) {
        header('Content-Type: text/plain;charset=UTF-8');
		$this->load->model('Verb_model');
        foreach ($this->Verb_model->get_hints($constraint) as $row)
		{
            echo $row["verb"];
			echo "\r\n";
		}
    }
    public function get($name) {
        header('Content-Type: text/plain;charset=UTF-8');
        $this->load->model('Verb_model');
        $verb = $this->Verb_model->get(base64_decode($name));
		if ($verb)
		{
			echo $verb->verb;
			echo "\r\n";
			echo $verb->content;
		}
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
                    echo "\r\n";
                }
                $this->Verb_model->verb = trim($currentLine);
                $this->Verb_model->content = "";
            } else {
                $this->Verb_model->content .= $currentLine;
            }
            $i++;
        }
        $this->Verb_model->insert();
        echo $this->Verb_model->verb;
        echo "\r\n";
        fclose($file);
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */