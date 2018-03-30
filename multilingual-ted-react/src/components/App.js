import React, {Component} from 'react';
import Header from './Header';
import TalkSummary from './TalkSummary';
import Filter from './Filter';
import {Divider} from 'antd';

class App extends Component {
    render() {
        return (
            <div>
                <Header/>
                <Divider/>
                <TalkSummary/>
                <Divider/>
                <Filter/>
            </div>
        );
    }
}

export default App;
