<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Mobile extends MY_Controller {

  
    public function version() {
        
        $this->output
                ->set_content_type('application/json')
                ->set_output(json_encode(array('version' => 1)));
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */