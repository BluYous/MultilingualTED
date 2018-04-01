import React, {Component} from 'react';
import {Col, Row} from 'antd';

class Header extends Component {
    render() {
        return (
            <div style={{width: '1100px'}}>
                <Row type="flex" justify="center">
                    <Col span={2}>
                        <svg xmlns="http://www.w3.org/2000/svg" width="96" height="54" viewBox="0, 0, 96, 54">
                            <title>TED</title>
                            <rect x="0" y="0" width="640" height="360" fill="none"/>
                            <path
                                d="M21.244 21.053h-6.761V14.85h21.012v6.203h-6.762V39.15h-7.489V21.053zm15.414-6.203h20.43v6.203H44.147v2.992h12.941v5.837H44.147v3.065h12.941v6.203h-20.43v-24.3zm21.666 0h12.287c8.071 0 10.906 5.984 10.906 12.114 0 7.443-3.926 12.186-12.36 12.186H58.324v-24.3zm7.489 18.097h2.908c4.653 0 5.308-3.794 5.308-6.056 0-1.533-.509-5.765-5.89-5.765H65.74l.073 11.821z"
                                fill="#E62B1E"/>
                        </svg>
                    </Col>
                    <Col span={22}>
                        <span style={{
                            color: '#999',
                            fontSize: '24px',
                            fontFamily: '"Helvetica Neue Custom","Helvetica Neue",Helvetica,Arial,sans-serif',
                            fontWeight: 200,
                            lineHeight: '54px',
                        }}>Ideas worth spreading</span>
                    </Col>
                </Row>
            </div>
        )
    }
}

export default Header;