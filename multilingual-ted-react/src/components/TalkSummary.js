import React, {Component} from 'react';
import axios from 'axios';
import {ajaxAddress} from "../common";

class TalkSummary extends Component {
    constructor(props) {
        super(props);
        this.state = {
            talksNum: 0,
        }
    }
    
    componentWillMount() {
        axios.get(ajaxAddress + "/api/talksNum")
            .then(res => {
                let talksNum = res.data;
                this.setState({
                    talksNum
                })
            })
            .catch(e => {
                console.log(e);
            });
    }
    
    render() {
        let {talksNum} = this.state;
        return (
            <div>
                <div style={{fontSize: '50px'}}>
                    <span><b>{talksNum}+ talks </b></span>
                    <span>to stir your curiosity</span>
                </div>
                <div style={{fontSize: '24px'}}>Find just the right one</div>
            </div>
        )
    }
}

export default TalkSummary;