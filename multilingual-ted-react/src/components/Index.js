import React, {Component} from 'react';
import {Divider} from 'antd';
import TalkSummary from './TalkSummary';
import Filter from './Filter';
import TalkFilterList from "./TalkFilterList";

class Index extends Component {
    render() {
        return (
            <div>
                <TalkSummary/>
                <Divider/>
                <Filter/>
                <Divider/>
                <TalkFilterList/>
            </div>
        )
    }
}

export default Index;