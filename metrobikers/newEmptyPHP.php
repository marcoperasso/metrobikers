<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
class Pages extends CI_Controller {

    public function test($a, $b) {
        $this->view($a . $b);
    }

    public function view($page = 'home') {
        if (!file_exists('application/views/pages/' . $page . '.php')) {
            
            show_404();
        }

        $data['title'] = ucfirst($page); 

        $this->load->view('templates/header', $data);
        $this->load->view('pages/' . $page, $data);
        $this->load->view('templates/footer', $data);
    }

}
?>
