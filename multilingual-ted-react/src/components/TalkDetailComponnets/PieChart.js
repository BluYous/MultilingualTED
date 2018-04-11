import React from 'react';
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/legend';
import echarts from 'echarts/lib/echarts';
import 'echarts/lib/chart/pie';


export default class PieChart extends React.Component {
    
    initPieChart = () => {
        const {data, dataName} = this.props;
        let myChart = echarts.init(this.refs.pieChart);
        let options = this.setPieOption(data, dataName);
        myChart.setOption(options)
    };
    setPieOption = (data, dataName) => {
        return {
            title: {
                text: "Ratings",
                left: "center",
                x: 'center',
            },
            tooltip: {
                trigger: 'item',
                formatter: "{b} : {c} ({d}%)"
            },
            legend: {
                orient: 'vertical',
                right: 'right',
                data: dataName
            },
            series: [
                {
                    type: 'pie',
                    radius: '55%',
                    center: ['50%', '60%'],
                    data: data,
                    itemStyle: {
                        emphasis: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    }
                }
            ]
        }
    };
    
    componentDidMount() {
        this.initPieChart()
    }
    
    componentDidUpdate() {
        this.initPieChart()
    }
    
    render() {
        return (
            <div style={{width: "100%", height: "100%"}}>
                <div ref="pieChart" style={{width: "100%", height: "100%"}}/>
            </div>
        )
    }
}