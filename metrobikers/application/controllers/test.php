<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Test extends MY_Controller {

    public function index() {
       phpinfo();
        //$this->load->model('User_model');

       // $this->User_model->get_user("marco.perasso@microarea.it");
       // set_user($this->User_model);
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */
