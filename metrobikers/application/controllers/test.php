<?php

if (!defined('BASEPATH'))
    exit('No direct script access allowed');

class Test extends MY_Controller {

    public function index() {
        echo my_base_url();
        $this->load->model('User_model');

        $this->User_model->get_user("marco.perasso@microarea.it");
        $_SESSION["user"] = serialize($this->User_model);
    }

}

/* End of file welcome.php */
/* Location: ./application/controllers/welcome.php */