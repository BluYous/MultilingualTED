import React, {Component} from 'react';
import axios from 'axios';

const common = require('../common');

class App extends Component {
    componentDidMount() {
        axios.get(common.ajaxAddress+"/ted/api")
            .then(res => {
                console.log(res.data);
            })
            .catch(e => {
                console.log(e);
            });
    }
    
    render() {
        return (
            <div>
                <p>
                    To get started, edit <code>src/App.js</code> and save to reload.
                </p>
            </div>
        );
    }
}

export default App;
